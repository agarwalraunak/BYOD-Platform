package com.device.service.rest.api;

import java.util.Date;

import javax.crypto.SecretKey;

import com.device.exception.InvalidResponseAuthenticatorException;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;
import com.device.service.rest.representation.UserServiceAuthenticationRequest;

public interface IServiceUserAuthenticationAPI {

	/**
	 * @param appSessionKey
	 * @param appLoginName
	 * @param username
	 * @param requestAuthenticator
	 * @param userSessionID
	 * @return
	 */
	UserServiceAuthenticationRequest createUserServiceAuthenticationRequest(
			SecretKey appSessionKey, String appLoginName,
			String username, Date requestAuthenticator, String userSessionID);

	/**
	 * @param appSessionKey
	 * @param username
	 * @param requestAuthenticator
	 * @param encUserSessionID
	 * @param encResponseAuthenticator
	 * @param encExpiryTimeStr
	 * @param appSession
	 * @return
	 * @throws InvalidResponseAuthenticatorException
	 */
	UserSession processUserServiceAuthenticationResponse(
			SecretKey appSessionKey, String username,
			Date requestAuthenticator, String encUserSessionID,
			String encResponseAuthenticator, String encExpiryTimeStr,
			AppSession appSession) throws InvalidResponseAuthenticatorException;

}
