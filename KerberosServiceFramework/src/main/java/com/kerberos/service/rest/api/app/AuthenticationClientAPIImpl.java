/**
 * 
 */
package com.kerberos.service.rest.api.app;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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

import com.kerberos.rest.representation.device.AppAuthenticationResponse;
import com.kerberos.rest.representation.device.UserLoginResponse;
import com.kerberos.service.models.AppSession;
import com.kerberos.service.models.AppSessionDirectory;
import com.kerberos.service.models.KerberosSessionManager;
import com.kerberos.service.models.UserSession;
import com.kerberos.service.rest.api.kerberos.IKerberosAuthenticationAPI;
import com.kerberos.service.rest.client.KerberosAuthenticationClient;
import com.kerberos.service.rest.client.KerberosServiceTicketClient;
import com.kerberos.service.rest.exceptions.InvalidInputException;
import com.kerberos.service.rest.exceptions.ServiceUnavailableException;
import com.kerberos.service.util.ActiveDirectory.IActiveDirectory;
import com.kerberos.service.util.dateutil.IDateUtil;
import com.kerberos.service.util.encryption.IEncryptionUtil;
import com.kerberos.service.util.hashing.HashUtilImpl.HashingTechqniue;
import com.kerberos.service.util.hashing.IHashUtil;

/**
 * @author raunak
 *
 */
public class AuthenticationClientAPIImpl  implements IAuthenticationClientAPI{
	
	private static Logger log = Logger.getLogger(AuthenticationClientAPIImpl.class);
	
	private @Autowired KerberosSessionManager kerberosSessionManager;
	private @Autowired KerberosAuthenticationClient kerberosAuthenticationClient;
	private @Autowired KerberosServiceTicketClient kerberosServiceTicketClient;
	private @Autowired IKerberosAuthenticationAPI iAuthenticationAPI;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IHashUtil iHashUtil;
	private @Autowired AppSessionDirectory appSessionDirectory; 
	private @Autowired IActiveDirectory iActiveDirectory;
	
	public enum ServiceTicketAttributes{
		USERNAME, SERVICE_SESSION_ID, SERVICE_TICKET_EXPIRATION_TIME_STR
	}
	
	@Override
	public AppAuthenticationResponse processAppAuthenticationRequest(String serviceTicketPacket, String encAuthenticator, SecretKey serviceKey, Map<String, String> responseData) throws InvalidAttributeValueException, InvalidInputException{
		
		log.debug("Entering processAppAuthenticationRequest method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(serviceTicketPacket, encAuthenticator)){
			log.error("Invalid input parameter to processAppAuthenticationRequest");
			throw new InvalidAttributeValueException("Invalid input parameter to processAppAuthenticationRequest");
		}
		
		Map<ServiceTicketAttributes, String> serviceTicketAttributes = retrieveServiceTicketPacketAttributes(serviceTicketPacket, serviceKey);
		
		String username = serviceTicketAttributes.get(ServiceTicketAttributes.USERNAME);
		String serviceSessionID = serviceTicketAttributes.get(ServiceTicketAttributes.SERVICE_SESSION_ID);
		String serviceTicketExpirationString = serviceTicketAttributes.get(ServiceTicketAttributes.SERVICE_TICKET_EXPIRATION_TIME_STR);
		
		if (!validateServiceTicket(username, serviceSessionID, serviceTicketExpirationString)){
			log.error("Invalid Service Ticket. Request Denied");
			throw new InvalidInputException("Invalid Service Ticket found. Request denied", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		SecretKey serviceSessionKey = iEncryptionUtil.generateSecretKey(serviceSessionID);
		String requestAuthenticatorStr = iEncryptionUtil.decrypt(serviceSessionKey, encAuthenticator)[0];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
				
		if (!iDateUtil.validateAuthenticator(requestAuthenticator)){
			log.error("Invalid request authenticator found. Request Denied");
			throw new InvalidInputException("Invalid authenticator found. Request Denied", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		//Check if the session for the application already exists else create
		AppSession appSession = appSessionDirectory.findAppSessionByAppID(username);
		if (appSession == null){
			appSession = appSessionDirectory.createAppSession(serviceSessionID, username);
		}
		appSession.addAuthenticator(requestAuthenticator);
		
		return createAppAuthenticationResponse(appSession, requestAuthenticator, serviceSessionKey, null);
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
	public AppAuthenticationResponse createAppAuthenticationResponse(AppSession appSession, Date requestAuthenticator, SecretKey serviceSessionKey, Map<String, String> responseData) throws InvalidAttributeValueException {
		
		log.debug("Entering createKeyRequestResponse");
		
		if (requestAuthenticator == null || serviceSessionKey == null){
			log.error("Invalid input parameter to createKeyRequestResponse");
			throw new InvalidAttributeValueException("Invalid input parameter to createKeyRequestResponse");
		}
		
		Date responseAuthenticator = iDateUtil.createResponseAuthenticator(requestAuthenticator);
		appSession.addAuthenticator(requestAuthenticator);
		appSession.addAuthenticator(responseAuthenticator);
		String[] encryptedData = iEncryptionUtil.encrypt(serviceSessionKey, appSession.getAppSessionID(),  iDateUtil.generateStringFromDate(responseAuthenticator));
		String encAppSessionID = encryptedData[0];
		String encResponseAuthenticator  = encryptedData[1];
		
		//Create the response
		AppAuthenticationResponse response = new AppAuthenticationResponse();
		response.setEncResponseAuthenticator(encResponseAuthenticator);
		response.setEncAppSessionID(encAppSessionID);
		
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
		
		if (new Date().after(iDateUtil.generateDateFromString(serviceTicketExpirationString))){
			return false;
		}
		
		log.debug("Returning from validateServiceTicket method");
		
		return true;
	}

	@Override
	public UserLoginResponse processUserLoginRequest(String encUsername, String encPassword, String encAppSessionID, String encRequestAuthenticator, String appUsername) throws InvalidAttributeValueException{
		
		log.debug("Entering processUserLoginRequest");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(encUsername, encPassword, encAppSessionID, encRequestAuthenticator, appUsername)){
			log.error("Invalid input parameter provided to processUserLoginRequest");
			throw new InvalidAttributeValueException("Invalid input parameter provided to processUserLoginRequest");
		}
		
		AppSession appSession = appSessionDirectory.findAppSessionByAppID(appUsername);
		if (appSession == null){
			throw new InvalidInputException("Request from unauthenticated app found", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		String appServiceSessionID = appSession.getAppServiceSessionID();
		SecretKey serviceSessionKey = iEncryptionUtil.generateSecretKey(appServiceSessionID);
		
		String appSessionID = iEncryptionUtil.decrypt(serviceSessionKey, encAppSessionID)[0];
		
		
		if(!iEncryptionUtil.validateDecryptedAttributes(appSessionID) || !appSessionID.equals(appSession.getAppSessionID())){
			log.error("Invalid request found. Request failed to validate");
			throw new InvalidInputException("Request failed to validate", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSessionID);
		
		String[] decryptedData = iEncryptionUtil.decrypt(appSessionKey, encUsername, encPassword, encRequestAuthenticator);
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			log.error("Invalid request found. Request failed to validate");
			throw new InvalidInputException("Request failed to validate", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		String username = decryptedData[0];
		String password = decryptedData[1];
		String requestAuthenticatorStr = decryptedData[2];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		
		if (!iDateUtil.validateAuthenticator(requestAuthenticator)){
			log.error("Invalid request found. Request failed to validate");
			throw new InvalidInputException("Request failed to validate", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		String dbPassword = null;
		try {
			dbPassword = iActiveDirectory.findPasswordForUser(username);
		} catch (NamingException | IOException e) {
			log.error("Error processing the request, LDAP connection failed. Failed to authenticate the username. Detailed exception attached below: \n "+e.getMessage());
			e.printStackTrace();
			throw new ServiceUnavailableException("Error processing the request, LDAP connection failed. Failed to authenticate the username. Please try again later ", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		UserSession userSession = null;
		try {
			if (dbPassword != null && !dbPassword.isEmpty() && dbPassword.equals(iHashUtil.bytetoString(iHashUtil.getHashWithSalt(password, HashingTechqniue.SSHA256, iHashUtil.stringToByte(username))))){
				//Check if user session exists
				userSession = appSession.findUserSessionByUsername(username);
				//If exists don't create the new one
				if (userSession == null)
					userSession = appSession.createUserSession(username, iHashUtil.getSessionKey());
			}
			//TODO:Handle User Authentication Failed!
			else{
				log.debug("User Authentication failed!");
			}
		} catch (NoSuchAlgorithmException e) {
			log.error("Error processing user authentication request. Detailed exception is attached below: \n"+e.getMessage());
			e.printStackTrace();
			throw new ServiceUnavailableException("Error processing request. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
			
		log.debug("Returning from processUserLoginRequest method");
		
		return createUserLoginResponse(appSession, userSession, requestAuthenticator, appSessionKey);
	}
	
	public UserLoginResponse createUserLoginResponse(AppSession appSession, UserSession userSession, Date requestAuthenticator, SecretKey appSessionKey) throws InvalidAttributeValueException{
		
		log.debug("Entering createUserLoginResponse method");
		
		if (userSession == null || requestAuthenticator == null){
			log.error("Invalid input parameter provided to createUserLoginRequset");
			throw new InvalidAttributeValueException("Invalid input parameter provided to createUserLoginRequset");
		}
		
		Date responseAuthenticator = iDateUtil.createResponseAuthenticator(requestAuthenticator);
		
		appSession.addAuthenticator(requestAuthenticator);
		appSession.addAuthenticator(responseAuthenticator);
		
		userSession.addAuthenticator(requestAuthenticator);
		userSession.addAuthenticator(responseAuthenticator);
		
		String responseAuthenticatorStr = iDateUtil.generateStringFromDate(responseAuthenticator);
		
		String[] encryptedData = iEncryptionUtil.encrypt(appSessionKey, responseAuthenticatorStr, userSession.getUsername(), userSession.getUserSessionID());
		String encResponseAuthenticator = encryptedData[0];
		String encUsername = encryptedData[1];
		String encUserSessionID = encryptedData[2];
		
		UserLoginResponse loginResponse = new UserLoginResponse();
		loginResponse.setEncResponseAuthenticator(encResponseAuthenticator);
		loginResponse.setEncUsername(encUsername);
		loginResponse.setEncUserSessionID(encUserSessionID);
		
		return loginResponse;
	}
}