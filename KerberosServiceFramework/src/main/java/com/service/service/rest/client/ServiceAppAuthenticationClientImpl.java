/**
 * 
 */
package com.service.service.rest.client;

import java.io.IOException;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.app.rest.representation.AppAuthenticationRequest;
import com.service.app.rest.representation.AppAuthenticationResponse;
import com.service.exception.RestClientException;
import com.service.exception.common.InternalSystemException;
import com.service.model.kerberos.ServiceTicket;
import com.service.model.service.ServiceSession;
import com.service.service.rest.api.IServiceAppAuthenticationAPI;
import com.service.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.service.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.service.util.connectionmanager.IConnectionManager;
import com.service.util.dateutil.IDateUtil;
import com.service.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */
@Component
public class ServiceAppAuthenticationClientImpl implements IServiceAppAuthenticationClient{

private static Logger log = Logger.getLogger(ServiceAppAuthenticationClientImpl.class);
	
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
	 * @throws InternalSystemException 
	 */
	@Override
	public ServiceSession authenticateAppServiceTicket(String url, ServiceTicket serviceTicket) throws InternalSystemException{
		
		log.debug("Entering authenticateAppServiceTicket");
		
		if (url == null || url.isEmpty() || serviceTicket == null){
			log.error("Invalid input parameter provided to authenticateAppServiceTicket");
			return null;
		}
		
		ServiceSession serviceSession = serviceTicket.getServiceSession();
		if (serviceSession != null){
			return serviceSession;
		}
		
		String requestAuthenticator = iDateUtil.createAuthenticator();
		SecretKey serviceSessionKey = iEncryptionUtil.generateSecretKey(serviceTicket.getServiceSessionID());
		AppAuthenticationRequest request = iServiceAppAuthenticationAPI.createAppAuthenticationRequest(serviceSessionKey, serviceTicket.getEncServiceTicket(), requestAuthenticator);
		if (request == null){
			throw new InternalSystemException();
		}
		
		AppAuthenticationResponse response;
		try {
			response = (AppAuthenticationResponse)iConnectionManager.generateRequest(url, RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, AppAuthenticationResponse.class, iConnectionManager.generateJSONStringForObject(request));
		} catch (IOException | RestClientException e) {
			log.error("Application Authentication Failed\n"+e.getMessage());
			e.printStackTrace();
			throw new InternalSystemException();
		}		
		serviceSession = iServiceAppAuthenticationAPI.processAuthenticateAppResponse(response.getEncAppSessionID(), response.getEncResponseAuthenticator(), response.getEncExpiryTime(),
				iDateUtil.generateDateFromString(requestAuthenticator),
				serviceTicket, serviceSessionKey);
		if (serviceSession == null){
			throw new InternalSystemException();
		}
		
		log.debug("Returning from authenticateAppServiceTicket method");
		
		return serviceSession;
	}
}
