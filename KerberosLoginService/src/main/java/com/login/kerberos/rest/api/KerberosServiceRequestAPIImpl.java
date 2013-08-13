package com.login.kerberos.rest.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.kerberos.model.KerberosAppSession;
import com.login.kerberos.model.KerberosSessionManager;
import com.login.kerberos.model.ServiceTicket;
import com.login.kerberos.model.TGT;
import com.login.kerberos.rest.api.KerberosAuthenticationAPIImpl.ServiceTicketResponseAttributes;
import com.login.kerberos.rest.client.KerberosAuthenticationClient;
import com.login.kerberos.rest.client.KerberosServiceTicketClient;
import com.login.kerberos.rest.representation.ServiceTicketRequest;
import com.login.kerberos.rest.representation.ServiceTicketResponse;
import com.login.rest.exceptions.ServiceUnavailableException;
import com.login.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.login.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.login.util.connectionmanager.IConnectionManager;
import com.login.util.dateutil.IDateUtil;
import com.login.util.encryption.IEncryptionUtil;
import com.login.util.hashing.IHashUtil;


/**
 * @author raunak
 *
 */
@Component
public class KerberosServiceRequestAPIImpl implements IKerberosServiceRequestAPI {
	
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IHashUtil iHashUtil;
	private @Autowired IConnectionManager iConnectionManager;
	private @Autowired KerberosServiceTicketClient kerberosServiceTicketClient;
	private @Autowired KerberosSessionManager kerberosSessionManager;
	private @Autowired KerberosAuthenticationClient authenticationClient;
	
	private static Logger log = Logger.getLogger(KerberosServiceRequestAPIImpl.class);
	
	@Override
	public ServiceTicket checkAppAuthenticationAndGetServiceTicket(String serviceName) throws ServiceUnavailableException{
		
		log.debug("Entering getKeyServerServiceTicketForApp method");
		
		KerberosAppSession appSession = kerberosSessionManager.getAppSession();
		ServiceTicket serviceTicket = null;
		if (appSession == null){
			try {
				if (!authenticationClient.kerberosAuthentication()){
					log.error("Unable to authenticate the app");
					throw new ServiceUnavailableException("Error processing the request. Please try again later!", Response.Status.SERVICE_UNAVAILABLE, MediaType.TEXT_HTML);
				}
			} catch (InvalidAttributeValueException e) {
				log.error("Unable to authenticate the app. Detailed exception is attached: "+e.getMessage());
				e.printStackTrace();
				throw new ServiceUnavailableException("Error processing the request. Please try again later!", Response.Status.SERVICE_UNAVAILABLE, MediaType.TEXT_HTML);
			}
			
			appSession = kerberosSessionManager.getAppSession();
			if (appSession == null){
				log.error("Error processing the request, Authenticated Application session does not exist");
				throw new ServiceUnavailableException("Error processing the request. Please try again later!", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
			}
		}
			
		TGT tgt = appSession.getTgt();
		if (tgt == null){
			log.error("Error processing the request, Authenticated Application session does not exist");
			throw new ServiceUnavailableException("Error processing the request. Please try again later!", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		serviceTicket = tgt.findActiveServiceTicketByServiceName(serviceName);
		if (serviceTicket == null){
			serviceTicket = kerberosServiceTicketClient.getServiceTicketForApp(serviceName);
			if (serviceTicket == null){
				log.error("Error processing the request, failed to get the Key Server Service Ticket for Application");
				throw new ServiceUnavailableException("Error processing the request. Please try again later!", Response.Status.SERVICE_UNAVAILABLE, MediaType.TEXT_HTML);
			}
		}
		
		log.debug("Returning from getKeyServerServiceTicketForApp method");

		return serviceTicket;
	}
	
	@Override
	public Map<ServiceTicketResponseAttributes, String> requestServiceTicketForApp(String url, String encAppTGTPacket, String serviceName, String sessionKey) throws IOException, InvalidAttributeValueException {
		
		log.debug("Entering requestServiceTicketForApp method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(url, encAppTGTPacket, serviceName, sessionKey)){
			log.error("Invalid input parameter to requestServiceTicketForApp");
			throw new InvalidAttributeValueException("Invalid input parameter to requestServiceTicketForApp");
		}
		
		String authenticator = iDateUtil.createAuthenticator();
		
		SecretKey key = iEncryptionUtil.generateSecretKey(sessionKey);
		String[] encryptedData  = iEncryptionUtil.encrypt(key, serviceName, authenticator);
		String encServiceName  = encryptedData[0];
		String encAuthenticator = encryptedData[1];
		
		ServiceTicketRequest request = new ServiceTicketRequest(encAppTGTPacket, encServiceName, encAuthenticator);
		String requestString = iConnectionManager.generateJSONStringForObject(request);
		
		ServiceTicketResponse response = (ServiceTicketResponse)iConnectionManager.generateRequest(url, RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, ServiceTicketResponse.class, requestString);
		if (response == null){
			return null;
		}
		
		log.debug("Returning from requestServiceTicketForApp method");
		
		return processServiceTicketResponse(response, authenticator, key);
	}
	
	/**
	 * @param response
	 * @param authenticator
	 * @param key
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	public Map<ServiceTicketResponseAttributes, String> processServiceTicketResponse(ServiceTicketResponse response, String authenticator, SecretKey key) throws InvalidAttributeValueException {
		
		log.debug("Entering processServiceTicketResponse method");
		
		if (response == null || authenticator == null || authenticator.isEmpty() || key == null){
			log.error("Invalid input parameter to processServiceTicketResponse");
			throw new InvalidAttributeValueException("Invalid input parameter to processServiceTicketResponse");
		}
		
		Map<ServiceTicketResponseAttributes, String> responseAttributes = decryptServiceTicketResponse(response, key);
		if (responseAttributes == null){
			return null;
		}
		
		String serviceTicketPacket = responseAttributes.get(ServiceTicketResponseAttributes.SERVICE_TICKET_PACKET); 
		String serviceSessionID = responseAttributes.get(ServiceTicketResponseAttributes.SERVICE_SESSION_ID);
		String serviceName = responseAttributes.get(ServiceTicketResponseAttributes.SERVICE_NAME);
		String decAuthenticator = responseAttributes.get(ServiceTicketResponseAttributes.AUTHENTICATOR);
		String expirtyTime = responseAttributes.get(ServiceTicketResponseAttributes.EXPIRY_TIME);

		if (!iEncryptionUtil.validateDecryptedAttributes(serviceTicketPacket, serviceSessionID, serviceName, decAuthenticator, expirtyTime) 
				|| !iDateUtil.validateAuthenticator(iDateUtil.generateDateFromString(decAuthenticator), iDateUtil.generateDateFromString(authenticator))){
			return null;
		}
		
		return responseAttributes;
	}	

	
	/**
	 * @param response
	 * @param key
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	public Map<ServiceTicketResponseAttributes, String> decryptServiceTicketResponse(ServiceTicketResponse response, SecretKey key) throws InvalidAttributeValueException{
		
		log.debug("Entering method decryptServiceTicketResponse");
		
		if (response == null || key == null){
			log.error("Invalid Input parameter to decryptServiceTicketResponse");
			throw new InvalidAttributeValueException("Invalid Input parameter to decryptServiceTicketResponse");
		}
		
		String[] decryptedData  = iEncryptionUtil.decrypt(key, response.getEncServiceTicket(), response.getEncServiceSessionID(),
				response.getEncServiceName(), response.getEncAuthenticator(), response.getEncExpiryTime());
		String serviceTicketPacket = decryptedData[0];
		String serviceSessionID = decryptedData[1];
		String serviceName = decryptedData[2];
		String authenticator = decryptedData[3];
		String expiryTimeString = decryptedData[4];
		
		Map<ServiceTicketResponseAttributes, String> responseAttributes = new HashMap<ServiceTicketResponseAttributes, String>();
		
		responseAttributes.put(ServiceTicketResponseAttributes.SERVICE_TICKET_PACKET, serviceTicketPacket);
		responseAttributes.put(ServiceTicketResponseAttributes.SERVICE_NAME, serviceName);
		responseAttributes.put(ServiceTicketResponseAttributes.SERVICE_SESSION_ID, serviceSessionID);
		responseAttributes.put(ServiceTicketResponseAttributes.AUTHENTICATOR, authenticator);
		responseAttributes.put(ServiceTicketResponseAttributes.EXPIRY_TIME, expiryTimeString);
		
		return responseAttributes;
	}
	
}
