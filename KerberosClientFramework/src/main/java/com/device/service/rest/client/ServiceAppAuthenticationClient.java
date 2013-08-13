/**
 * 
 */
package com.device.service.rest.client;

import java.io.IOException;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.device.kerberos.model.ServiceTicket;
import com.device.service.model.AppSession;
import com.device.service.rest.api.IServiceAppAuthenticationAPI;
import com.device.service.rest.representation.AppAuthenticationRequest;
import com.device.service.rest.representation.AppAuthenticationResponse;
import com.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.device.util.connectionmanager.IConnectionManager;
import com.device.util.dateutil.IDateUtil;
import com.device.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */
@Component
public class ServiceAppAuthenticationClient {

private static Logger log = Logger.getLogger(ServiceAppAuthenticationClient.class);
	
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IConnectionManager iConnectionManager;
	private @Autowired IServiceAppAuthenticationAPI iServiceAppAuthenticationAPI;
	
	/**
	 * @param url
	 * @param serviceTicket
	 * @return
	 * @throws InvalidAttributeValueException
	 * @throws IOException
	 */
	public AppSession authenticateAppServiceTicket(String url, ServiceTicket serviceTicket) throws InvalidAttributeValueException, IOException{
		
		log.debug("Entering authenticateAppServiceTicket");
		
		if (url == null || url.isEmpty() || serviceTicket == null){
			log.error("Invalid input parameter provided to authenticateAppServiceTicket");
			throw new InvalidAttributeValueException("Invalid input parameter provided to authenticateAppServiceTicket");
		}
		
		AppSession appSession = serviceTicket.getAppServiceSession();
		if (appSession != null){
			return appSession;
		}
		
		String requestAuthenticator = iDateUtil.createAuthenticator();
		SecretKey serviceSessionKey = iEncryptionUtil.generateSecretKey(serviceTicket.getServiceSessionID());
		AppAuthenticationRequest request = iServiceAppAuthenticationAPI.createAppAuthenticationRequest(serviceSessionKey, serviceTicket.getEncServiceTicket(), requestAuthenticator);
		
		AppAuthenticationResponse response = (AppAuthenticationResponse)iConnectionManager.generateRequest(url, RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, AppAuthenticationResponse.class, iConnectionManager.generateJSONStringForObject(request));
		
		appSession = iServiceAppAuthenticationAPI.processAuthenticateAppResponse(response.getEncAppSessionID(), response.getEncResponseAuthenticator(), iDateUtil.generateDateFromString(requestAuthenticator),
				serviceTicket, serviceSessionKey);
		
		log.debug("Returning from authenticateAppServiceTicket method");
		
		return appSession;
	}
}
