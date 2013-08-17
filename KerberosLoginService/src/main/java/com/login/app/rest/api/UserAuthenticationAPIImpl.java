/**
 * 
 */
package com.login.app.rest.api;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.app.rest.representation.UserLoginResponse;
import com.login.exception.AuthenticationRestService.DecryptUserLoginRequestParamsException;
import com.login.exception.common.AuthenticatorValidationException;
import com.login.exception.common.InternalSystemException;
import com.login.exception.common.InvalidRequestException;
import com.login.kerberos.rest.api.IKerberosAuthenticationAPI;
import com.login.kerberos.rest.client.IKerberosAuthenticationClient;
import com.login.kerberos.rest.client.IKerberosRequestServiceTicketClient;
import com.login.model.SessionDirectory;
import com.login.model.app.AppSession;
import com.login.model.app.UserSession;
import com.login.util.ActiveDirectory.IActiveDirectory;
import com.login.util.dateutil.IDateUtil;
import com.login.util.encryption.IEncryptionUtil;
import com.login.util.hashing.HashUtilImpl.HashingTechqniue;
import com.login.util.hashing.IHashUtil;

/**
 * @author raunak
 *
 */
@Component
public class UserAuthenticationAPIImpl  implements IUserAuthenticationAPI{
	
	public enum UserLoginRequestParams{
		USERNAME, PASSWORD, REQUEST_AUTHENTICATOR
	}
	
	private static Logger log = Logger.getLogger(UserAuthenticationAPIImpl.class);
	
	private @Autowired IKerberosAuthenticationClient iKerberosAuthenticationClient;
	private @Autowired IKerberosRequestServiceTicketClient iKerberosServiceTicketClient;
	private @Autowired IKerberosAuthenticationAPI iAuthenticationAPI;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IHashUtil iHashUtil;
	private @Autowired SessionDirectory sessionDirectory; 
	private @Autowired IActiveDirectory iActiveDirectory;
	
	@Override
	public Map<UserLoginRequestParams, String> decryptAndValidateRequestAttributes(SecretKey kerberosServiceSessionKey, AppSession appSession, String encAppSessionID, String encUsername, String encPassword, String encRequestAuthenticator) throws AuthenticatorValidationException, DecryptUserLoginRequestParamsException, InvalidRequestException{
		
		log.debug("Entering decryptAndValidateRequestAttributes");
		
		//Validate method arguments
		if (kerberosServiceSessionKey == null || appSession == null || !iEncryptionUtil.validateDecryptedAttributes(encAppSessionID, encUsername, encPassword, encRequestAuthenticator)){
			log.error("Invalid input parameter provided to decryptAndValidateRequestAttributes");
			throw new IllegalArgumentException("Invalid input parameter provided to decryptAndValidateRequestAttributes");
		}
		
		//Decrypt the App Session ID from request
		String appSessionID = iEncryptionUtil.decrypt(kerberosServiceSessionKey, encAppSessionID)[0];
		
		//Validating the App Session ID from request
		if(!iEncryptionUtil.validateDecryptedAttributes(appSessionID) || !appSessionID.equals(appSession.getSessionID())){
			log.error("Invalid request found. Request failed to validate");
			throw new  InvalidRequestException();
		}
		
		//Generate key using App Session ID
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSessionID);
		
		//Decrypting the Request Params
		String[] decryptedData = iEncryptionUtil.decrypt(appSessionKey, encUsername, encPassword, encRequestAuthenticator);
		//Validating the Decrypted Params
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			log.error("Failed to decyrpt the UserLoginRequest parameters!");
			throw new DecryptUserLoginRequestParamsException();
		}
		
		String username = decryptedData[0];
		String password = decryptedData[1];
		String requestAuthenticatorStr = decryptedData[2];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		
		//Validating the Authenticator from the App Session
		if (!appSession.validateAuthenticator(requestAuthenticator)){
			log.error("User Login Request Authenticator validation failed, for User: "+username);
			throw new AuthenticatorValidationException();
		}
		
		Map<UserLoginRequestParams, String> requestAttributes = new HashMap<>();
		requestAttributes.put(UserLoginRequestParams.USERNAME, username);
		requestAttributes.put(UserLoginRequestParams.PASSWORD, password);
		requestAttributes.put(UserLoginRequestParams.REQUEST_AUTHENTICATOR, requestAuthenticatorStr);
		
		log.debug("Returning from decryptAndValidateRequestAttributes");
		
		return requestAttributes;
	}
	
	@Override
	public UserSession authenticateUser(String username, String password, String clientIP, AppSession appSession) throws InternalSystemException{
		
		log.debug("Entering authenticateUser");
		
		if (username == null || username.isEmpty() || password == null || password.isEmpty()){
			log.error("Invalid Input Parameter provided to authenticateUser");
			throw new IllegalArgumentException("Invalid Input Parameter provided to authenticateUser");
		}
		
		String dbPassword = null;
		try {
			dbPassword = iActiveDirectory.findPasswordForUser(username);
		} catch (NamingException e) {
			log.error("Invalid User! Username does not exist");
			e.printStackTrace();
			return null;
		} catch(IOException e){
			log.error("Error processing the request, LDAP connection failed. Failed to authenticate the user. Detailed exception attached below: \n "+e.getMessage());
			e.printStackTrace();
			throw new InternalSystemException();
		}
		
		//If dbpassword does not exist means user is not registered 
		if (dbPassword == null || dbPassword.isEmpty()){
			return null;
		}
		
		//Preparing password of the user
		String userPassword;
		try {
			userPassword = iHashUtil.bytetoString(iHashUtil.getHashWithSalt(password, HashingTechqniue.SSHA256, iHashUtil.stringToByte(username)));
		} catch (NoSuchAlgorithmException e) {
			log.error("Error encountered while generating hash in the authenticateUser method");
			e.printStackTrace();
			throw new InternalSystemException();
		}
		
		//Check if the Passwords are equal else return null
		if (!dbPassword.equals(userPassword)){
			return null;
		}
		
		//Check if the Session for user already exists else create
		UserSession userSession = appSession.findActiveUserSessionByUsername(username);
		if (userSession == null){
			userSession = appSession.createUserSession(username, iHashUtil.getSessionKey(), clientIP);
		}
		
		return userSession;
	}
	
	@Override
	public UserLoginResponse createUserLoginResponse(String username, UserSession userSession) throws InternalSystemException{
		
		log.debug("Entering createUserLoginResponse method");
		
		//Validating input arguments
		if (username == null || username.isEmpty()){
			log.error("Invalid input parameter provided to createUserLoginRequset");
			throw new IllegalArgumentException("Invalid input parameter provided to createUserLoginRequset");
		}
		
		String userSessionID = null;
		if (userSession != null){
			userSessionID = userSession.getSessionID();
		}
		
		UserLoginResponse loginResponse = new UserLoginResponse();
		loginResponse.setEncUsername(username);
		loginResponse.setEncUserSessionID(userSessionID);
		loginResponse.setEncExpiryTime(iDateUtil.generateStringFromDate(userSession.getExpiryTime()));
		
		return loginResponse;
	}
}