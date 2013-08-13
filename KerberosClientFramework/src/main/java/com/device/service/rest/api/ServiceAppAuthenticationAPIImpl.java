/**
 * 
 */
package com.device.service.rest.api;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.device.applicationdetailservice.ApplicationDetailService;
import com.device.kerberos.model.ServiceTicket;
import com.device.service.model.AppSession;
import com.device.service.rest.representation.AppAuthenticationRequest;
import com.device.util.connectionmanager.IConnectionManager;
import com.device.util.dateutil.IDateUtil;
import com.device.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */
@Component
public class ServiceAppAuthenticationAPIImpl implements IServiceAppAuthenticationAPI{
	
	private static Logger log = Logger.getLogger(ServiceAppAuthenticationAPIImpl.class);
	
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired ApplicationDetailService applicationDetailService;
	private @Autowired IConnectionManager iConnectionManager;

	@Override
	public AppAuthenticationRequest createAppAuthenticationRequest(SecretKey serviceSessionKey, String serviceTicketPacket, String requestAuthenticator) throws InvalidAttributeValueException{
		
		log.debug("Entering createAppAuthenticationRequest method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(serviceTicketPacket, requestAuthenticator) || serviceSessionKey == null){
			log.error("Invalid input parameter provided to createAppAuthenticationRequest");
			throw new InvalidAttributeValueException("Invalid input parameter provided to createAppAuthenticationRequest");
		}
		
		String encRequestAuthenticator = iEncryptionUtil.encrypt(serviceSessionKey, requestAuthenticator)[0];
		
		AppAuthenticationRequest appAuthenticationRequest = new AppAuthenticationRequest();
		appAuthenticationRequest.setEncAuthenticator(encRequestAuthenticator);
		appAuthenticationRequest.setServiceTicketPacket(serviceTicketPacket);
		
		log.debug("Returning from createAppAuthenticationRequest");
		
		return appAuthenticationRequest;
	}
	
	@Override
	public AppSession processAuthenticateAppResponse(String encAppSessionID, String encResponseAuthenticator, Date requestAuthenticator, ServiceTicket serviceTicket, SecretKey serviceSessionKey) throws InvalidAttributeValueException{
		
		log.debug("Entering processAuthenticateAppResponse method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(encAppSessionID, encResponseAuthenticator) || serviceSessionKey == null || serviceTicket == null){
			log.error("Invalid input parameter provided to processAuthenticateAppResponse");
			throw new InvalidAttributeValueException("Invalid input parameter provided to processAuthenticateAppResponse");
		}
		
		String[] decryptedData = iEncryptionUtil.decrypt(serviceSessionKey, encAppSessionID, encResponseAuthenticator);
		
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			return null;
		}
		
		String appSessionID = decryptedData[0];
		String responseAuthenticator = decryptedData[1];
		
		//Create the App Service Session
		AppSession appSession = serviceTicket.createAppServiceSession(appSessionID);
		
		appSession.addAuthenticator(requestAuthenticator);
		appSession.addAuthenticator(iDateUtil.generateDateFromString(responseAuthenticator));
		
		log.debug("Returning from processAuthenticateAppResponse");
		
		return appSession;
		
	}
}
