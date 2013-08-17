package com.device.service.rest.api;

import java.util.Date;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.device.exception.InvalidResponseAuthenticatorException;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;
import com.device.service.rest.representation.UserServiceAuthenticationRequest;
import com.device.service.rest.representation.UserServiceAuthenticationResponse;
import com.device.util.dateutil.IDateUtil;
import com.device.util.encryption.IEncryptionUtil;

@Component
public class ServiceUserAuthenticationAPIImpl implements IServiceUserAuthenticationAPI{
	
	private static Logger log = Logger.getLogger(ServiceUserAuthenticationAPIImpl.class);
	
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	
	@Override
	public UserServiceAuthenticationRequest createUserServiceAuthenticationRequest(SecretKey appSessionKey, String appLoginName, String username, 
			Date requestAuthenticator, String userSessionID){
		
		log.debug("Entering createUserServiceAuthenticationRequest");
		
		if (appSessionKey == null || !iEncryptionUtil.validateDecryptedAttributes(appLoginName, username, userSessionID) || requestAuthenticator == null){
			log.error("Invalid input argument provided to createUserServiceAuthenticationRequest");
			throw new IllegalArgumentException("Invalid input argument provided to createUserServiceAuthenticationRequest");
		}
		
		String requestAuthenticatorStr = iDateUtil.generateStringFromDate(requestAuthenticator);
		
		String[] encryptedData = iEncryptionUtil.encrypt(appSessionKey, userSessionID, requestAuthenticatorStr, username);
		String encUserSessionID = encryptedData[0];
		String encAuthenticator = encryptedData[1];
		String encUsername = encryptedData[2];
		
		//Creating User Service Authentication Request
		UserServiceAuthenticationRequest request = new UserServiceAuthenticationRequest();
		request.setAppID(appLoginName);
		request.setEncUserSessionID(encUserSessionID);
		request.setEncAuthenticator(encAuthenticator);
		request.setEncUsername(encUsername);
		
		log.debug("Returning from createUserServiceAuthenticationRequest");
		
		return request;
	}

	@Override
	public UserSession processUserServiceAuthenticationResponse(SecretKey appSessionKey, String username, 
			Date requestAuthenticator, String encUserSessionID, String encResponseAuthenticator, String encExpiryTimeStr, AppSession appSession) throws InvalidResponseAuthenticatorException{
		
		if (appSessionKey == null || requestAuthenticator == null || !iEncryptionUtil.validateDecryptedAttributes(username, encResponseAuthenticator)){
			log.error("Invalid input argument provided to processUserServiceAuthenticationResponse");
			throw new IllegalArgumentException("Invalid input argument provided to processUserServiceAuthenticationResponse");
		}
		
		if (encUserSessionID == null || encUserSessionID.isEmpty() || encExpiryTimeStr == null || encExpiryTimeStr.isEmpty()){
			return null;
		}
		
		//Decrypting the response
		String[] decResponseData = iEncryptionUtil.decrypt(appSessionKey, encUserSessionID, encResponseAuthenticator, encExpiryTimeStr);
		String userServiceSessionID = decResponseData[0];
		String responseAuthenticatorStr = decResponseData[1];
		String expiryTimeStr = decResponseData[2];
		Date responseAuthenticator = iDateUtil.generateDateFromString(responseAuthenticatorStr);
		
		//validating the authenticator
		if (!iEncryptionUtil.validateDecryptedAttributes(responseAuthenticatorStr) && iDateUtil.validateAuthenticator(responseAuthenticator, requestAuthenticator)){
			log.error("Unauthorized response recevied, Authenticator failed to validte");
			throw new InvalidResponseAuthenticatorException(UserServiceAuthenticationResponse.class, "serviceUserAuthentication", getClass());
		}
		
		//Check if the User Service Session ID is not null or empty else User failed to authenticate
		if (!iEncryptionUtil.validateDecryptedAttributes(userServiceSessionID)){
			return null;
		}
		
		//Create a session for the user and add authenticators to App Service and User Service Session
		UserSession userSession = appSession.createUserServiceSession(username, userServiceSessionID, iDateUtil.generateDateFromString(expiryTimeStr));
		userSession.addAuthenticator(requestAuthenticator);
		appSession.addAuthenticator(requestAuthenticator);
		
		userSession.addAuthenticator(responseAuthenticator);
		appSession.addAuthenticator(responseAuthenticator);
		
		return userSession;
	}
	
}
