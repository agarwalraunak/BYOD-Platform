package com.device.login.rest.api;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import com.device.login.rest.representation.UserLoginRequest;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;

public interface ILoginServerUserAuthenticationAPI {

	/**
	 * @param username
	 * @param password
	 * @param requestAuthenticationStr
	 * @param appSessionID
	 * @param appUsername
	 * @param appSessionIDKey
	 * @param serviceSessionKey
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	UserLoginRequest createUserLoginRequest(String username, String password,
			String requestAuthenticationStr, String appSessionID,
			String appUsername, SecretKey appSessionIDKey,
			SecretKey serviceSessionKey) throws InvalidAttributeValueException;

	/**
	 * @param encUsername
	 * @param encUserSessionID
	 * @param encResponseAuthenticator
	 * @param appSession
	 * @param requestAuthenticator
	 * @param appSessionIDKey
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	UserSession processUserLoginResponse(String encUsername,
			String encUserSessionID, String encResponseAuthenticator,
			AppSession appSession, Date requestAuthenticator,
			SecretKey appSessionIDKey) throws InvalidAttributeValueException;

}
