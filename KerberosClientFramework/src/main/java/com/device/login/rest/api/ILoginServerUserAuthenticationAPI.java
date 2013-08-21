package com.device.login.rest.api;

import java.util.Date;

import javax.crypto.SecretKey;

import com.device.exception.InvalidResponseAuthenticatorException;
import com.device.login.rest.representation.UserLoginRequest;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;

/**
 * This interface provides the method necessary for <strong>User Authentication</strong>. 
 * It is used by the <code>ILoginServerUserAuthenticationClient</code>
 * 
 * @author raunak
 *
 */
public interface ILoginServerUserAuthenticationAPI {

	/**
	 * This method creates a <code>UserLoginRequest</code>
	 * @param <code>String</code> username
	 * @param <code>String</code> password
	 * @param <code>String</code> Request Authenticator
	 * @param <code>String</code> App Session ID created by the Login Service
	 * @param <code>String</code> App Login Name
	 * @param <code>SecretKey</code> Key generated from App Session IDs 
	 * @param <code>SecretKey</code> Key generated from the Kerberos Service Session ID
	 * @return
	 * <code>UserLoginRequest</code>
	 */
	UserLoginRequest createUserLoginRequest(String username, String password,
			String requestAuthenticationStr, String appSessionID,
			String appUsername, SecretKey appSessionIDKey,
			SecretKey serviceSessionKey) ;



	/**
	 * @param <code>String</code> Encrypted User name from User Login Response
	 * @param <code>String</code> Encrypted User Session ID
	 * @param <code>String</code> Encrypted Response Authenticator
	 * @param <code>String</code> Encrypted Expiry Time
	 * @param <code>AppSession</code> appSession
	 * @param <code>Data</code> requestAuthenticator
	 * @param <code>SecretKey</code> appSessionIDKey
	 * @return
	 * <code>UserSession</code> or null if the <strong>User Authentication fails!</strong>
	 * @throws InvalidResponseAuthenticatorException
	 */
	UserSession processUserLoginResponse(String encUsername,
			String encUserSessionID, String encResponseAuthenticator,
			String encExpiryTime, AppSession appSession,
			Date requestAuthenticator, SecretKey appSessionIDKey)
			throws InvalidResponseAuthenticatorException;

}
