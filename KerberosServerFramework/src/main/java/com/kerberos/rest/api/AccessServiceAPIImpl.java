/**
 * 
 */
package com.kerberos.rest.api;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kerberos.db.model.ServiceTicket;
import com.kerberos.db.service.IServiceTicketService;
import com.kerberos.exceptions.InvalidInputException;
import com.kerberos.exceptions.ServiceUnavailableException;
import com.kerberos.rest.representation.AccessServiceResponse;
import com.kerberos.rest.representation.KeyServerRequest;
import com.kerberos.util.ActiveDirectory.IActiveDirectory;
import com.kerberos.util.ActiveDirectory.ActiveDirectoryImpl.SecretKeyType;
import com.kerberos.util.dateutil.IDateUtil;
import com.kerberos.util.encryption.IEncryptionUtil;
import com.kerberos.util.hashing.IHashUtil;
import com.kerberos.util.keyserver.KeyServerUtil;

/**
 * @author raunak
 *
 */
public class AccessServiceAPIImpl implements IAccessServiceAPI{

	private static Logger log = Logger.getLogger(AccessServiceAPIImpl.class);
	
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IServiceTicketService iServiceTicketService;
	private @Autowired ITGSApi iTGSApi; 
	private @Autowired IActiveDirectory iActiveDirectory;
	private @Autowired KeyServerUtil keyServerUtil;
	private @Autowired IHashUtil iHashUtil;
	
	public enum ServiceTicketAttributes{
		USERNAME, SERVICE_SESSION_ID, SERVICE_TICKET_EXPIRATION_TIME_STR
	}
	
	@Override
	public AccessServiceResponse processKeyServerRequest(KeyServerRequest request, SecretKey serviceKey) throws InvalidAttributeValueException, ServiceUnavailableException {
		
		log.debug("Entering processKeyServerRequest method");
		
		if (request == null || serviceKey == null){
			log.error("Invalid input parameter to processKeyServerRequest");
			throw new InvalidAttributeValueException("Invalid Input parameter to processKeyServerRequest");
		}
		
		//Retrieve decrypted ServiceTicketPaket attributes
		Map<ServiceTicketAttributes, String> serviceTicketAttributes = retrieveServiceTicketPacketAttributes(request.getEncServiceTicket(), serviceKey);
		if (serviceTicketAttributes == null){
			return null;
		}
		
		String username = serviceTicketAttributes.get(ServiceTicketAttributes.USERNAME);
		String serviceSessionID = serviceTicketAttributes.get(ServiceTicketAttributes.SERVICE_SESSION_ID);
		String serviceTicketExpirationString = serviceTicketAttributes.get(ServiceTicketAttributes.SERVICE_TICKET_EXPIRATION_TIME_STR);
		
		//Validate the output for the decrypted attributes & Validate the Service Ticket
		if (!validateServiceTicket(username, serviceSessionID, serviceTicketExpirationString)){
			return null;
		}
		
		//Decrypt the authenticator and key alias from the request
		SecretKey serviceSessionKey = iEncryptionUtil.generateSecretKey(serviceSessionID);
		String[] decryptedData = iEncryptionUtil.decrypt(serviceSessionKey, request.getEncAuthenticator(), request.getKeyType());
		String requestAuthenticatorString  = decryptedData[0];
		String keyTypeValue = decryptedData[1];
		if (!iEncryptionUtil.validateDecryptedAttributes(requestAuthenticatorString, keyTypeValue)){
			return null;
		}
		
		//Validate the authenticator
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorString);
		if (!iDateUtil.validateAuthenticator(requestAuthenticator)){
			return null;
		}
		
		//Get the key from the keyserver
		SecretKey requestedKey = null;
		try {
			requestedKey = keyServerUtil.getKeyFromKeyStore(username, SecretKeyType.valueOf(keyTypeValue));
		} catch (InvalidAttributeValueException e){
			log.error("Failed to fetch the key from the key server. Detailed Exception is attached: "+e.getMessage());
			e.printStackTrace();
			throw new InvalidInputException("Error processing request. Bad requset found", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML);
		} catch (NoSuchAlgorithmException
				| UnrecoverableEntryException | KeyStoreException
				| CertificateException | NamingException | IOException e) {
			log.error("Failed to fetch the key from the key server. Detailed Exception is attached: "+e.getMessage());
			e.printStackTrace();
			throw new ServiceUnavailableException("Failed to process the request. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		if (requestedKey == null){
			throw new ServiceUnavailableException("Failed to process the request. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		log.debug("Returning from processKeyServerRequest method");

		//Preparing the response
		return createKeyRequestResponse(requestAuthenticator, keyTypeValue, requestedKey, serviceSessionKey);
		
	}
	
	/**
	 * @param encServiceTicketPacket
	 * @param serviceKey
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	public Map<ServiceTicketAttributes, String> retrieveServiceTicketPacketAttributes(String encServiceTicketPacket, SecretKey serviceKey) throws InvalidAttributeValueException {
		
		log.debug("Entering retrieveServiceTicketPacketAttributes method");
		
		if (encServiceTicketPacket == null || encServiceTicketPacket.isEmpty() || serviceKey == null){
			log.error("Invalid input parameter to method retrieveServiceTicketPacketAttributes");
			throw new InvalidAttributeValueException("Invalid input parameter to method retrieveServiceTicketPacketAttributes");
		}
		
		String serviceTicketPacket = iEncryptionUtil.decrypt(serviceKey, encServiceTicketPacket)[0];
		if (serviceTicketPacket == null){
			return null;
		}
		
		String[] serviceTicketParts = serviceTicketPacket.split(",");
		if (serviceTicketParts.length != 3){
			return null;
		}
		String username = serviceTicketParts[0];
		String serviceSessionID = serviceTicketParts[1];
		String serviceTicketExpirationString = serviceTicketParts[2];
		
		Map<ServiceTicketAttributes, String> serviceTicketAttributes = new HashMap<ServiceTicketAttributes, String>();
		serviceTicketAttributes.put(ServiceTicketAttributes.USERNAME, username);
		serviceTicketAttributes.put(ServiceTicketAttributes.SERVICE_SESSION_ID, serviceSessionID);
		serviceTicketAttributes.put(ServiceTicketAttributes.SERVICE_TICKET_EXPIRATION_TIME_STR, serviceTicketExpirationString);
		
		log.debug("Returning from retrieveServiceTicketPacketAttributes method");
		
		return serviceTicketAttributes;
	}
	
	
	/**
	 * @param requestAuthenticator
	 * @param keyTypeValue
	 * @param requestedKey
	 * @param serviceSessionKey
	 * @return
	 */
	public AccessServiceResponse createKeyRequestResponse(Date requestAuthenticator, String keyTypeValue, SecretKey requestedKey, SecretKey serviceSessionKey) throws InvalidAttributeValueException {
		
		log.debug("Entering createKeyRequestResponse");
		
		if (requestAuthenticator == null || keyTypeValue == null || keyTypeValue.isEmpty() || requestedKey == null || serviceSessionKey == null){
			log.error("Invalid input parameter to createKeyRequestResponse");
			throw new InvalidAttributeValueException("Invalid input parameter to createKeyRequestResponse");
		}
		
		//Preparing the response data
		Map<String, String> responseData = new HashMap<String, String>();
		String requestedKeyString = iHashUtil.bytetoString(requestedKey.getEncoded());
		String encRequestedKeyString = iEncryptionUtil.encrypt(serviceSessionKey, requestedKeyString)[0];
		responseData.put(keyTypeValue, encRequestedKeyString);
		
		//Get the response authenticator
		String encResponseAuthenticator = iEncryptionUtil.encrypt(serviceSessionKey, iDateUtil.generateStringFromDate(iDateUtil.createResponseAuthenticator(requestAuthenticator)))[0];

		//Create the response
		AccessServiceResponse response = new AccessServiceResponse();
		response.setResponseData(responseData);
		response.setEncResponseAuthenticator(encResponseAuthenticator);
		
		log.debug("Returning from createKeyRequestResponse method");
		
		return response;
	}
	
	/**
	 * @param username
	 * @param serviceSessionID
	 * @param serviceTicketExpirationString
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	public boolean validateServiceTicket(String username, String serviceSessionID, String serviceTicketExpirationString) throws InvalidAttributeValueException{
		
		log.debug("Entering validateServiceTicket method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(username, serviceSessionID, serviceTicketExpirationString)){
			log.error("Input parameter can not be null to validateServiceTicket");
			throw new InvalidAttributeValueException("Input parameter can not be null to validateServiceTicket");
		}
		
		ServiceTicket serviceTicket = iServiceTicketService.findServiceTicketByID(serviceSessionID);
		if (serviceTicket == null || !serviceTicket.getTgt().getUsername().equals(username)){
			return false;
		}
		
		try {
			if (iActiveDirectory.findPasswordForApp(username) == null){
				return false;
			}
		} catch (NamingException | IOException e) {
			log.error("Error fetching password for the given username "+e.getMessage());
			e.printStackTrace();
			return false;
		}
		
		if (new Date().after(iDateUtil.generateDateFromString(serviceTicketExpirationString))){
			return false;
		}
		
		log.debug("Returning from validateServiceTicket method");
		
		return true;
	}

}
