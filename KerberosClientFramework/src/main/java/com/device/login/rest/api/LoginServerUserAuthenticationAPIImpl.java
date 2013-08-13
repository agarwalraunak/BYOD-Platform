/**
 * 
 */
package com.device.login.rest.api;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.device.applicationdetailservice.ApplicationDetailService;
import com.device.login.rest.representation.UserLoginRequest;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;
import com.device.util.connectionmanager.IConnectionManager;
import com.device.util.dateutil.IDateUtil;
import com.device.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */
@Component
public class LoginServerUserAuthenticationAPIImpl implements ILoginServerUserAuthenticationAPI {
	
	private static Logger log = Logger.getLogger(LoginServerUserAuthenticationAPIImpl.class);
	
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired ApplicationDetailService applicationDetailService;
	private @Autowired IConnectionManager iConnectionManager;
	
	@Override
	public UserLoginRequest createUserLoginRequest(String username, String password, String requestAuthenticationStr, String appSessionID, String appUsername, SecretKey appSessionIDKey, SecretKey serviceSessionKey) throws InvalidAttributeValueException {
		
		log.debug("Entering createUserLoginRequest");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(username, password, requestAuthenticationStr, appSessionID, appUsername) || appSessionIDKey == null || serviceSessionKey == null){
			log.error("Invalid input parameter provided to createUserLoginRequest");
			throw new InvalidAttributeValueException("Invalid input parameter provided to createUserLoginRequest");
		}
		
		String[] encryptedData = iEncryptionUtil.encrypt(appSessionIDKey, username, password, requestAuthenticationStr);
		String encUsername = encryptedData[0];
		String encPassword = encryptedData[1];
		String encRequestAuthenticatorStr = encryptedData[2];
		
		String encAppSessionID= iEncryptionUtil.encrypt(serviceSessionKey, appSessionID)[0];
		
		UserLoginRequest request = new UserLoginRequest();
		request.setAppID(appUsername);
		request.setEncAppSessionID(encAppSessionID);
		request.setEncAuthenticator(encRequestAuthenticatorStr);
		request.setEncPassword(encPassword);
		request.setEncUsername(encUsername);
		
		return request;
	}
	
	@Override
	public UserSession processUserLoginResponse(String encUsername, String encUserSessionID, String encResponseAuthenticator, AppSession appSession, Date requestAuthenticator, SecretKey appSessionIDKey) throws InvalidAttributeValueException {
		
		log.debug("Entering processUserLoginResponse method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(encUsername, encUserSessionID, encResponseAuthenticator)){
			log.error("Invalid input parameter provided to processUserLoginrResponse method");
			throw new InvalidAttributeValueException("Invalid input parameter provided to processUserLoginrResponse method");
		}
		
		String[] decryptedData = iEncryptionUtil.decrypt(appSessionIDKey, encUsername, encUserSessionID, encResponseAuthenticator);
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			log.debug("Validation of UserLoginResponse failed. User session would not be created");
			return null;
		}
		
		String username = decryptedData[0];
		String userSessionID = decryptedData[1];
		String responseAuthenticator = decryptedData[2];
		
		UserSession userSession = appSession.createUserServiceSession(username, userSessionID);
		
		userSession.addAuthenticator(requestAuthenticator);
		userSession.addAuthenticator(iDateUtil.generateDateFromString(responseAuthenticator));
		
		log.debug("Returning from processUserLoginRequest");
		
		return userSession;
	}

}
