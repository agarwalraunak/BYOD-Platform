/**
 * 
 */
package com.service.service.rest.client;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.kerberos.rest.client.IKerberosAuthenticationClient;
import com.service.kerberos.rest.client.IKerberosServiceTicketClient;
import com.service.model.kerberos.KerberosAppSession;
import com.service.model.service.ServiceSession;
import com.service.rest.exception.common.AuthenticatorValidationException;
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
	public Map<String, String> contactAnotherService(String url, RequestMethod requestMethod, ContentType contentType, String serviceName, KerberosAppSession kerberosAppSession, String serviceSessionID,
			ServiceSession serviceSession, Map<String, String> requestData) throws IOException, AuthenticatorValidationException{
		
		log.debug("Entering contactAnotherService");
		
		if (url == null || url.isEmpty() || requestMethod == null || contentType == null || serviceName == null || serviceName.isEmpty() || kerberosAppSession == null 
				|| serviceSessionID == null || serviceSessionID.isEmpty() || serviceSession == null){
			
			log.error("Invalid Input parameter provided to contactAnotherService");
			return null;
		}
		
		Date requestAuthenticator = serviceSession.createAuthenticator();
		
		AppAccessServiceRequest request = iAppAccessServiceAPI.generateAppAccessServiceRequest(kerberosAppSession, serviceSessionID, serviceSession, requestAuthenticator, requestData);
		
		AppAccessServiceResponse response = (AppAccessServiceResponse)iConnectionManager.generateRequest(url, requestMethod, contentType, AppAccessServiceResponse.class, iConnectionManager.generateJSONStringForObject(request));

		log.debug("Returning from contactAnotherService");
		
		return iAppAccessServiceAPI.processAppAccessServiceResponse(response, requestAuthenticator, serviceSession);
	}
	
}
