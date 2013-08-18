/**
 * 
 */
package com.service.service.rest.client;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.exception.RestClientException;
import com.service.exception.common.AuthenticatorValidationException;
import com.service.exception.common.InternalSystemException;
import com.service.kerberos.rest.client.IKerberosAuthenticationClient;
import com.service.kerberos.rest.client.IKerberosServiceTicketClient;
import com.service.model.service.ServiceSession;
import com.service.service.rest.api.IAppAccessServiceAPI;
import com.service.service.rest.representation.AppAccessServiceRequest;
import com.service.service.rest.representation.AppAccessServiceResponse;
import com.service.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.service.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.service.util.connectionmanager.IConnectionManager;

/**
 * @author raunak
 *
 */

@Component
public class ServiceAccessAnotherServiceClientImpl implements IServiceAccessAnotherServiceClient {
	
	private static Logger log = Logger.getLogger(ServiceAccessAnotherServiceClientImpl.class);
	
	private @Autowired IKerberosAuthenticationClient iKerberosAuthenticationClient;
	private @Autowired IKerberosServiceTicketClient iKerberosServiceTicketClient;
	
	private @Autowired IAppAccessServiceAPI iAppAccessServiceAPI;
	
	private @Autowired IConnectionManager iConnectionManager;
	
	@Override
	public Map<String, String> contactAnotherService(String url, RequestMethod requestMethod, ContentType contentType, String serviceName, String serviceSessionID,
			ServiceSession serviceSession, Map<String, String> requestData) throws IOException, AuthenticatorValidationException, InternalSystemException{
		
		log.debug("Entering contactAnotherService");
		
		if (url == null || url.isEmpty() || requestMethod == null || contentType == null || serviceName == null || serviceName.isEmpty() 
				|| serviceSessionID == null || serviceSessionID.isEmpty() || serviceSession == null){
			
			log.error("Invalid Input parameter provided to contactAnotherService");
			return null;
		}
		
		Date requestAuthenticator = serviceSession.createAuthenticator();
		
		AppAccessServiceRequest request = iAppAccessServiceAPI.generateAppAccessServiceRequest(serviceSessionID, serviceSession, requestAuthenticator, requestData);
		
		AppAccessServiceResponse response;
		try {
			response = (AppAccessServiceResponse)iConnectionManager.generateRequest(url, requestMethod, contentType, AppAccessServiceResponse.class, iConnectionManager.generateJSONStringForObject(request));
		} catch (RestClientException e) {
			e.printStackTrace();
			if (e.getErrorCode() == Response.Status.UNAUTHORIZED.getStatusCode()){
				serviceSession.setActive(false);
			}
			throw new InternalSystemException();
		}

		log.debug("Returning from contactAnotherService");
		
		return iAppAccessServiceAPI.processAppAccessServiceResponse(response, requestAuthenticator, serviceSession);
	}
	
}
