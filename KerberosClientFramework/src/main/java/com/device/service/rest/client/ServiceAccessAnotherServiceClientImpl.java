/**
 * 
 */
package com.device.service.rest.client;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.device.exception.InvalidResponseAuthenticatorException;
import com.device.exception.RestClientException;
import com.device.kerberos.rest.client.IKerberosAuthenticationClient;
import com.device.service.model.AppSession;
import com.device.service.rest.api.IAppAccessServiceAPI;
import com.device.service.rest.representation.AppAccessServiceRequest;
import com.device.service.rest.representation.AppAccessServiceResponse;
import com.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.device.util.connectionmanager.IConnectionManager;

/**
 * @author raunak
 *
 */

@Component
public class ServiceAccessAnotherServiceClientImpl implements IServiceAccessAnotherServiceClient {
	
	private static Logger log = Logger.getLogger(ServiceAccessAnotherServiceClientImpl.class);
	
	private @Autowired IKerberosAuthenticationClient iKerberosAuthenticationClient;
	
	private @Autowired IAppAccessServiceAPI iAppAccessServiceAPI;
	
	private @Autowired IConnectionManager iConnectionManager;
	
	@Override
	public Map<String, String> contactAnotherService(String url, RequestMethod requestMethod, ContentType contentType, String serviceSessionID,
			AppSession appSession, Map<String, String> requestData) throws InvalidResponseAuthenticatorException, IOException, RestClientException{
		
		log.debug("Entering contactAnotherService");
		
		if (url == null || url.isEmpty() || requestMethod == null || contentType == null || serviceSessionID == null || serviceSessionID.isEmpty() || appSession == null){
			
			log.error("Invalid Input parameter provided to contactAnotherService");
			return null;
		}
		
		Date requestAuthenticator = appSession.createAuthenticator();
		
		AppAccessServiceRequest request = iAppAccessServiceAPI.generateAppAccessServiceRequest(serviceSessionID, appSession, requestAuthenticator, requestData);
		
		AppAccessServiceResponse response = (AppAccessServiceResponse)iConnectionManager.generateRequest(url, requestMethod, contentType, AppAccessServiceResponse.class, iConnectionManager.generateJSONStringForObject(request));

		log.debug("Returning from contactAnotherService");
		
		return iAppAccessServiceAPI.processAppAccessServiceResponse(response, requestAuthenticator, appSession);
	}
	
}
