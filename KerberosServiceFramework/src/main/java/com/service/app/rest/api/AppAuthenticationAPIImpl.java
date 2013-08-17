/**
 * 
 */
package com.service.app.rest.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.app.rest.representation.AppAuthenticationResponse;
import com.service.model.app.AppSession;
import com.service.util.dateutil.IDateUtil;
import com.service.util.encryption.IEncryptionUtil;
import com.service.util.hashing.IHashUtil;

/**
 * @author raunak
 *
 */
@Component
public class AppAuthenticationAPIImpl  implements IAppAuthenticationAPI{
	
	private static Logger log = Logger.getLogger(AppAuthenticationAPIImpl.class);
	
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IHashUtil iHashUtil;
	
	public enum ServiceTicketAttributes{
		APP_LOGIN_NAME, SERVICE_SESSION_ID, SERVICE_TICKET_EXPIRATION_TIME_STR
	}
	
	@Override
	public Map<ServiceTicketAttributes, String> decryptServiceTicketPacket(String encServiceTicketPacket, SecretKey serviceKey)  {
		
		log.debug("Entering retrieveServiceTicketPacketAttributes method");
		
		//Return null if the input parameters are not valid
		if (encServiceTicketPacket == null || encServiceTicketPacket.isEmpty() || serviceKey == null){
			log.error("Invalid input parameter to method retrieveServiceTicketPacketAttributes");
			return null;
		}
		
		//Decrypt the service ticket packet
		String serviceTicketPacket = iEncryptionUtil.decrypt(serviceKey, encServiceTicketPacket)[0];
		//Return null if the service ticket is null
		if (serviceTicketPacket == null){
			return null;
		}
		
		//Return null if the service ticket is not valid
		String[] serviceTicketParts = serviceTicketPacket.split(",");
		if (serviceTicketParts.length != 3){
			return null;
		}
		String username = serviceTicketParts[0];
		String serviceSessionID = serviceTicketParts[1];
		String serviceTicketExpirationString = serviceTicketParts[2];
		
		Map<ServiceTicketAttributes, String> serviceTicketAttributes = new HashMap<ServiceTicketAttributes, String>();
		serviceTicketAttributes.put(ServiceTicketAttributes.APP_LOGIN_NAME, username);
		serviceTicketAttributes.put(ServiceTicketAttributes.SERVICE_SESSION_ID, serviceSessionID);
		serviceTicketAttributes.put(ServiceTicketAttributes.SERVICE_TICKET_EXPIRATION_TIME_STR, serviceTicketExpirationString);
		
		log.debug("Returning from retrieveServiceTicketPacketAttributes method");
		
		return serviceTicketAttributes;
	}
	
	
	@Override
	public boolean validateServiceTicket(String username, String serviceSessionID, String serviceTicketExpirationString) {
		
		log.debug("Entering validateServiceTicket method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(username, serviceSessionID, serviceTicketExpirationString)){
			log.error("Input parameter can not be null to validateServiceTicket");
			return false;
		}
		
		if (new Date().after(iDateUtil.generateDateFromString(serviceTicketExpirationString))){
			return false;
		}
		
		log.debug("Returning from validateServiceTicket method");
		
		return true;
	}
	
	@Override
	public AppAuthenticationResponse createAppAuthenticationResponse(AppSession appSession, Date requestAuthenticator, SecretKey serviceSessionKey)  {
		
		log.debug("Entering createKeyRequestResponse");
		
		if (appSession == null || requestAuthenticator == null || serviceSessionKey == null){
			log.error("Invalid input parameter to createKeyRequestResponse");
			return null;
		}
		
		Date responseAuthenticator = iDateUtil.createResponseAuthenticator(requestAuthenticator);
		appSession.addAuthenticator(requestAuthenticator);
		appSession.addAuthenticator(responseAuthenticator);
		String[] encryptedData = iEncryptionUtil.encrypt(serviceSessionKey, appSession.getSessionID(),  iDateUtil.generateStringFromDate(responseAuthenticator), iDateUtil.generateStringFromDate(appSession.getExpiryTime()));
		String encAppSessionID = encryptedData[0];
		String encResponseAuthenticator  = encryptedData[1];
		String encExpiryTime = encryptedData[2];
		
		//Create the response
		AppAuthenticationResponse response = new AppAuthenticationResponse();
		response.setEncResponseAuthenticator(encResponseAuthenticator);
		response.setEncAppSessionID(encAppSessionID);
		response.setEncExpiryTime(encExpiryTime);
		
		log.debug("Returning from createKeyRequestResponse method");
		
		return response;
	}
	
}