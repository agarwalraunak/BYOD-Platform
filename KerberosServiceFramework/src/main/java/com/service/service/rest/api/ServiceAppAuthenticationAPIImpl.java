/**
 * 
 */
package com.service.service.rest.api;

import java.util.Date;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.app.rest.representation.AppAuthenticationRequest;
import com.service.config.applicationdetailservice.ApplicationDetailService;
import com.service.model.kerberos.ServiceTicket;
import com.service.model.service.ServiceSession;
import com.service.util.connectionmanager.IConnectionManager;
import com.service.util.dateutil.IDateUtil;
import com.service.util.encryption.IEncryptionUtil;

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
	public AppAuthenticationRequest createAppAuthenticationRequest(SecretKey serviceSessionKey, String serviceTicketPacket, String requestAuthenticator) {
		
		log.debug("Entering createAppAuthenticationRequest method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(serviceTicketPacket, requestAuthenticator) || serviceSessionKey == null){
			log.error("Invalid input parameter provided to createAppAuthenticationRequest");
			return null;
		}
		
		String encRequestAuthenticator = iEncryptionUtil.encrypt(serviceSessionKey, requestAuthenticator)[0];
		
		AppAuthenticationRequest appAuthenticationRequest = new AppAuthenticationRequest();
		appAuthenticationRequest.setEncAuthenticator(encRequestAuthenticator);
		appAuthenticationRequest.setServiceTicketPacket(serviceTicketPacket);
		
		log.debug("Returning from createAppAuthenticationRequest");
		
		return appAuthenticationRequest;
	}
	
	@Override
	public ServiceSession processAuthenticateAppResponse(String encAppSessionID, String encResponseAuthenticator, Date requestAuthenticator, ServiceTicket serviceTicket, SecretKey serviceSessionKey) {
		
		log.debug("Entering processAuthenticateAppResponse method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(encAppSessionID, encResponseAuthenticator) || serviceSessionKey == null || serviceTicket == null){
			log.error("Invalid input parameter provided to processAuthenticateAppResponse");
			return null;
		}
		
		String[] decryptedData = iEncryptionUtil.decrypt(serviceSessionKey, encAppSessionID, encResponseAuthenticator);
		
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			log.error("Response Parametes failed to decyrpt for Application Authentication");
			return null;
		}
		
		String appSessionID = decryptedData[0];
		String responseAuthenticator = decryptedData[1];
		
		//Create the Service Session
		ServiceSession serviceSession = serviceTicket.createServiceSession(appSessionID);
		
		serviceSession.addAuthenticator(requestAuthenticator);
		serviceSession.addAuthenticator(iDateUtil.generateDateFromString(responseAuthenticator));
		
		log.debug("Returning from processAuthenticateAppResponse");
		
		return serviceSession;
		
	}
}
