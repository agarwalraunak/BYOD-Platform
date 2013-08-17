/**
 * 
 */
package com.device.login.rest.client;

import java.io.IOException;
import java.util.Date;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.device.applicationdetailservice.ApplicationDetailService;
import com.device.exception.InvalidResponseAuthenticatorException;
import com.device.exception.RestClientException;
import com.device.login.rest.api.ILoginServerUserAuthenticationAPI;
import com.device.login.rest.representation.UserLoginRequest;
import com.device.login.rest.representation.UserLoginResponse;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;
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
public class LoginServerUserAuthenticationClientImpl implements ILoginServerUserAuthenticationClient{
	
	private static Logger log = Logger.getLogger(LoginServerUserAuthenticationClientImpl.class);
	
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IConnectionManager iConnectionManager;
	private @Autowired ILoginServerUserAuthenticationAPI iLoginServerUserAuthenticationAPI;
	private @Autowired ApplicationDetailService applicationDetailService;
	
	
	@Override
	public UserSession authenticateUser(String url, AppSession appSession, String serviceSessionID, String username, String password) 
			throws IOException, RestClientException, InvalidResponseAuthenticatorException{
	
		log.debug("Entering authenticateUser method");
		
		if (url == null || url.isEmpty() || appSession == null || serviceSessionID == null || serviceSessionID.isEmpty() || username == null || username.isEmpty() || password == null || password.isEmpty()){
			log.error("Invalid input parameter provided to authenticateUser");
			throw new IllegalArgumentException("Invalid input parameter provided to authenticateUser");
		}
		
		UserSession userSession = appSession.findActiveUserServiceSessionByUsername(username);
		if (userSession != null){
			return userSession;
		}
		
		String appSessionID = appSession.getSessionID();
		SecretKey appSessionIDKey = iEncryptionUtil.generateSecretKey(appSessionID);
		SecretKey serviceSessionKey = iEncryptionUtil.generateSecretKey(serviceSessionID);
		
		Date requestAuthenticator = appSession.createAuthenticator();
		String requestAuthenticatorStr = iDateUtil.generateStringFromDate(requestAuthenticator);
		
		UserLoginRequest request = iLoginServerUserAuthenticationAPI.createUserLoginRequest(username, password, requestAuthenticatorStr, appSessionID, applicationDetailService.getAppLoginName() , appSessionIDKey, serviceSessionKey);
		UserLoginResponse response = (UserLoginResponse)iConnectionManager.generateRequest(url, RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, UserLoginResponse.class, iConnectionManager.generateJSONStringForObject(request));
		
		userSession = iLoginServerUserAuthenticationAPI.processUserLoginResponse(response.getEncUsername(), response.getEncUserSessionID(), response.getEncResponseAuthenticator(), response.getEncExpiryTime(), appSession, requestAuthenticator, appSessionIDKey);
		
		if (userSession == null){
			log.debug("Failed to create a service session for the user");
		}
		
		return userSession;
	}
}