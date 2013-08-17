package com.service.kerberos.rest.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.exception.RestClientException;
import com.service.exception.common.InternalSystemException;
import com.service.kerberos.rest.api.KerberosAuthenticationAPIImpl.ServiceTicketResponseAttributes;
import com.service.kerberos.rest.client.IKerberosAuthenticationClient;
import com.service.kerberos.rest.client.IKerberosServiceTicketClient;
import com.service.kerberos.rest.representation.ServiceTicketRequest;
import com.service.kerberos.rest.representation.ServiceTicketResponse;
import com.service.model.SessionDirectory;
import com.service.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.service.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.service.util.connectionmanager.IConnectionManager;
import com.service.util.dateutil.IDateUtil;
import com.service.util.encryption.IEncryptionUtil;
import com.service.util.hashing.IHashUtil;

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
	private @Autowired SessionDirectory sessionDirectory;
	private @Autowired IKerberosAuthenticationClient iKerberosAuthenticationClient;
	private @Autowired IKerberosServiceTicketClient iKerberosServiceTicketClient;
	
	private static Logger log = Logger.getLogger(KerberosServiceRequestAPIImpl.class);
	
	@Override
	public Map<ServiceTicketResponseAttributes, String> requestServiceTicketForApp(String url, String encAppTGTPacket, String serviceName, String sessionKey) throws InternalSystemException{
		
		log.debug("Entering requestServiceTicketForApp method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(url, encAppTGTPacket, serviceName, sessionKey)){
			log.error("Invalid input parameter to requestServiceTicketForApp");
			return null;
		}
		
		String authenticator = iDateUtil.createAuthenticator();
		
		SecretKey key = iEncryptionUtil.generateSecretKey(sessionKey);
		String[] encryptedData  = iEncryptionUtil.encrypt(key, serviceName, authenticator);
		String encServiceName  = encryptedData[0];
		String encAuthenticator = encryptedData[1];
		
		ServiceTicketRequest request = new ServiceTicketRequest(encAppTGTPacket, encServiceName, encAuthenticator);
		String requestString = iConnectionManager.generateJSONStringForObject(request);
		
		ServiceTicketResponse response;
		try {
			response = (ServiceTicketResponse)iConnectionManager.generateRequest(url, RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, ServiceTicketResponse.class, requestString);
		} catch (IOException e) {
			log.error("Error fetching service ticket from kerberos\n"+e.getMessage());
			e.printStackTrace();
			throw new InternalSystemException();
		} catch (RestClientException e) {
			e.printStackTrace();
			if (e.getErrorCode() == Response.Status.UNAUTHORIZED.getStatusCode()){
				sessionDirectory.setKerberosAppSession(null);
			}
			throw new InternalSystemException();
		}
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
	 */
	public Map<ServiceTicketResponseAttributes, String> processServiceTicketResponse(ServiceTicketResponse response, String authenticator, SecretKey key) {
		
		log.debug("Entering processServiceTicketResponse method");
		
		if (response == null || authenticator == null || authenticator.isEmpty() || key == null){
			log.error("Invalid input parameter to processServiceTicketResponse");
			return null;
		}
		
		Map<ServiceTicketResponseAttributes, String> responseAttributes = decryptServiceTicketResponse(response, key);
		if (responseAttributes == null){
			return null;
		}
		
		String serviceTicketPacket = responseAttributes.get(ServiceTicketResponseAttributes.SERVICE_TICKET_PACKET); 
		String serviceSessionID = responseAttributes.get(ServiceTicketResponseAttributes.SERVICE_SESSION_ID);
		String serviceName = responseAttributes.get(ServiceTicketResponseAttributes.SERVICE_NAME);
		String decAuthenticator = responseAttributes.get(ServiceTicketResponseAttributes.AUTHENTICATOR);
		String expiryTime = responseAttributes.get(ServiceTicketResponseAttributes.EXPIRY_TIME);
		
		if (!iEncryptionUtil.validateDecryptedAttributes(serviceTicketPacket, serviceSessionID, serviceName, decAuthenticator, expiryTime) 
				|| !iDateUtil.validateAuthenticator(iDateUtil.generateDateFromString(decAuthenticator), iDateUtil.generateDateFromString(authenticator))){
			return null;
		}
		
		return responseAttributes;
	}	

	
	/**
	 * @param response
	 * @param key
	 * @return
	 */
	public Map<ServiceTicketResponseAttributes, String> decryptServiceTicketResponse(ServiceTicketResponse response, SecretKey key) {
		
		log.debug("Entering method decryptServiceTicketResponse");
		
		if (response == null || key == null){
			log.error("Invalid Input parameter to decryptServiceTicketResponse");
			return null;
		}
		
		String[] decryptedData  = iEncryptionUtil.decrypt(key, response.getEncServiceTicket(), response.getEncServiceSessionID(), 
				response.getEncServiceName(), response.getEncAuthenticator(), response.getEncExpiryTime());
		String serviceTicketPacket = decryptedData[0];
		String serviceSessionID = decryptedData[1];
		String serviceName = decryptedData[2];
		String authenticator = decryptedData[3];
		String expiryTimeStr = decryptedData[4];
		
		Map<ServiceTicketResponseAttributes, String> responseAttributes = new HashMap<ServiceTicketResponseAttributes, String>();
		
		responseAttributes.put(ServiceTicketResponseAttributes.SERVICE_TICKET_PACKET, serviceTicketPacket);
		responseAttributes.put(ServiceTicketResponseAttributes.SERVICE_NAME, serviceName);
		responseAttributes.put(ServiceTicketResponseAttributes.SERVICE_SESSION_ID, serviceSessionID);
		responseAttributes.put(ServiceTicketResponseAttributes.AUTHENTICATOR, authenticator);
		responseAttributes.put(ServiceTicketResponseAttributes.EXPIRY_TIME, expiryTimeStr);
		
		return responseAttributes;
	}
	
}
