package com.device.service.rest.client;

import java.io.IOException;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.device.applicationdetailservice.ApplicationDetailService;
import com.device.exception.ApplicationDetailServiceUninitializedException;
import com.device.exception.InvalidMethodArgumentValue;
import com.device.exception.InvalidResponseAuthenticatorException;
import com.device.exception.RestClientException;
import com.device.kerberos.model.KerberosSessionManager;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;
import com.device.service.rest.api.IServiceUserAuthenticationAPI;
import com.device.service.rest.representation.UserServiceAuthenticationRequest;
import com.device.service.rest.representation.UserServiceAuthenticationResponse;
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
public class ServiceUserAuthenticationClientImpl implements IServiceUserAuthenticationClient{

	private static Logger log = Logger.getLogger(ServiceUserAuthenticationClientImpl.class);
	
	private @Autowired ApplicationDetailService applicationDetailService;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IConnectionManager iConnectionManager;
	private @Autowired KerberosSessionManager kerberosSessionManager;
	
	private @Autowired IServiceUserAuthenticationAPI iServiceUserAuthenticationAPI;
	
	@Override
	public boolean serviceUserAuthentication(String serviceUserAuthenticationURL, UserSession userLoginServiceSession, AppSession appSession) throws IOException, RestClientException, InvalidResponseAuthenticatorException, ApplicationDetailServiceUninitializedException{
		
		log.debug("Entering serviceUserAuthentication");
		
		if (userLoginServiceSession == null || appSession == null || serviceUserAuthenticationURL == null || serviceUserAuthenticationURL.isEmpty()){
			log.error("Invalid input parameter to serviceUserAuthentication");
			throw new InvalidMethodArgumentValue(getClass().getName(), "serviceUserAuthentication");
		}
		
		String appID = applicationDetailService.getAppLoginName();
		if (appID == null || appID.isEmpty()){
			log.error("Application Detail Service is not properly initialized. Service User Authentication failed!");
			throw new ApplicationDetailServiceUninitializedException();
		}
		
		//Creating attributes required for User Service Authentication Request
		String username = userLoginServiceSession.getUsername();
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSession.getSessionID());
		Date requestAuthenticator = appSession.createAuthenticator();
		
		UserServiceAuthenticationRequest request = iServiceUserAuthenticationAPI.createUserServiceAuthenticationRequest(appSessionKey, appID, username, requestAuthenticator, userLoginServiceSession.getUserSessionID());
		
		//Sending the request and recieving the response
		//If the response Status is UNAUTHORIZED delete the App Session
		UserServiceAuthenticationResponse response;
		try {
			response = (UserServiceAuthenticationResponse)iConnectionManager.generateRequest(serviceUserAuthenticationURL, RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, UserServiceAuthenticationResponse.class, iConnectionManager.generateJSONStringForObject(request));
		} catch (RestClientException e) {
			if (e.getErrorCode() == Response.Status.UNAUTHORIZED.getStatusCode())
				kerberosSessionManager.getKerberosAppSession().deactiveAppSession(appSession);
			throw e;
		}
		
		UserSession userSession = iServiceUserAuthenticationAPI.processUserServiceAuthenticationResponse(appSessionKey, username, requestAuthenticator, response.getEncUserSessionID(), response.getEncResponseAuthenticator(), response.getEncExpiryTime(), appSession);
		log.debug("Returning from serviceUserAuthentication");
		
		return userSession != null;
	}
	
}
