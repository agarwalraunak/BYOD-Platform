package com.device.service.rest.client;

import java.io.IOException;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.device.applicationdetailservice.ApplicationDetailService;
import com.device.rest.exceptions.UnauthorizedResponseException;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;
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
public class ServiceUserAuthenticationClient {

	private static Logger log = Logger.getLogger(ServiceUserAuthenticationClient.class);
	
	private @Autowired ApplicationDetailService applicationDetailService;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IConnectionManager iConnectionManager;
	
	/**
	 * This method does Service Authentication of the User. It requires that User and App Login Service Session are already 
	 * created. Also, requires the URL for the service for which the authentication is being performed 
	 * @param serviceUserAuthenticationURL Web service URL to be called for User Service Authentication
	 * @param userLoginServiceSession User Login Service Session
	 * @param appSession App Service Session for the service for which the user is being authenticated
	 * @return boolean true if successfully authenticated else false
	 * @throws InvalidAttributeValueException
	 * @throws IOException
	 * @throws UnauthorizedResponseException
	 */
	public boolean serviceUserAuthentication(String serviceUserAuthenticationURL, UserSession userLoginServiceSession, AppSession appSession) throws InvalidAttributeValueException, IOException, UnauthorizedResponseException{
		
		log.debug("Entering serviceUserAuthentication");
		
		if (userLoginServiceSession == null || appSession == null || serviceUserAuthenticationURL == null || serviceUserAuthenticationURL.isEmpty()){
			log.error("Invalid input parameter to serviceUserAuthentication");
			throw new InvalidAttributeValueException("Invalid input parameter to serviceUserAuthentication");
		}
		
		String appID = applicationDetailService.getAppLoginName();
		if (appID == null || appID.isEmpty()){
			log.error("Application Detail Service is not properly initialized. Service User Authentication failed!");
			throw new InvalidAttributeValueException("Application Detail Service is not properly initialized. Service User Authentication failed!");
		}
		
		//Creating attributes required for User Service Authentication Request
		String username = userLoginServiceSession.getUsername();
		SecretKey appServiceSessionKey = iEncryptionUtil.generateSecretKey(appSession.getSessionID());
		Date requestAuthenticator = appSession.createAuthenticator();
		String requestAuthenticatorStr = iDateUtil.generateStringFromDate(requestAuthenticator);
		String[] encryptedData = iEncryptionUtil.encrypt(appServiceSessionKey, userLoginServiceSession.getUserSessionID(), requestAuthenticatorStr, username);
		String encUserSessionID = encryptedData[0];
		String encAuthenticator = encryptedData[1];
		String encUsername = encryptedData[2];
		
		//Creating User Service Authentication Request
		UserServiceAuthenticationRequest request = new UserServiceAuthenticationRequest();
		request.setAppID(appID);
		request.setEncUserSessionID(encUserSessionID);
		request.setEncAuthenticator(encAuthenticator);
		request.setEncUsername(encUsername);
		
		//Sending the request and recieving the response
		UserServiceAuthenticationResponse response = (UserServiceAuthenticationResponse)iConnectionManager.generateRequest(serviceUserAuthenticationURL, RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, UserServiceAuthenticationResponse.class, iConnectionManager.generateJSONStringForObject(request));
		
		//Retrieving attributes from the resposne
		String encResponseAuthenticator = response.getEncResponseAuthenticator();
		String encUserServiceSessionID = response.getEncUserSessionID();
		
		//Decrypting the response
		String[] decResponseData = iEncryptionUtil.decrypt(appServiceSessionKey, encUserServiceSessionID, encResponseAuthenticator);
		String userServiceSessionID = decResponseData[0];
		String responseAuthenticatorStr = decResponseData[1];
		Date responseAuthenticator = iDateUtil.generateDateFromString(responseAuthenticatorStr);
		
		//validating the authenticator
		if (!iEncryptionUtil.validateDecryptedAttributes(responseAuthenticatorStr) && iDateUtil.validateAuthenticator(responseAuthenticator, requestAuthenticator)){
			log.error("Unauthorized response recevied, Authenticator failed to validte");
			throw new UnauthorizedResponseException("Unauthorized response recevied, Authenticator failed to validte");
		}
		
		//Check if the User Service Session ID is not null or empty else User failed to authenticate
		if (!iEncryptionUtil.validateDecryptedAttributes(userServiceSessionID)){
			return false;
		}
		
		//Create a session for the user and add authenticators to App Service and User Service Session
		UserSession userSession = appSession.createUserServiceSession(username, userServiceSessionID);
		userSession.addAuthenticator(requestAuthenticator);
		appSession.addAuthenticator(requestAuthenticator);
		
		userSession.addAuthenticator(responseAuthenticator);
		appSession.addAuthenticator(responseAuthenticator);
		
		log.debug("Returning from serviceUserAuthentication");
		
		return true;
	}
	
}
