/**
 * 
 */
package com.login.app.rest.api;

import java.util.Map;

import javax.crypto.SecretKey;

import com.login.app.rest.api.UserAuthenticationAPIImpl.UserLoginRequestParams;
import com.login.app.rest.representation.UserLoginResponse;
import com.login.exception.AuthenticationRestService.DecryptUserLoginRequestParamsException;
import com.login.exception.common.AuthenticatorValidationException;
import com.login.exception.common.InternalSystemException;
import com.login.exception.common.InvalidRequestException;
import com.login.model.app.AppSession;
import com.login.model.app.UserSession;

/**
 * @author raunak
 *
 */
public interface IUserAuthenticationAPI {

	/**
	 * @param kerberosServiceSessionKey
	 * @param appSession
	 * @param encAppSessionID
	 * @param encUsername
	 * @param encPassword
	 * @param encRequestAuthenticator
	 * @return
	 * @throws AuthenticatorValidationException
	 * @throws DecryptUserLoginRequestParamsException
	 * @throws InvalidRequestException
	 */
	Map<UserLoginRequestParams, String> decryptAndValidateRequestAttributes(
			SecretKey kerberosServiceSessionKey, AppSession appSession,
			String encAppSessionID, String encUsername, String encPassword,
			String encRequestAuthenticator)
			throws AuthenticatorValidationException,
			DecryptUserLoginRequestParamsException, InvalidRequestException;


	/**
	 * @param username
	 * @param password
	 * @param clientIP
	 * @param appSession
	 * @return
	 * @throws InternalSystemException
	 */
	UserSession authenticateUser(String username, String password,
			String clientIP, AppSession appSession)
			throws InternalSystemException;

	/**
	 * @param username
	 * @param userSession
	 * @return
	 * @throws InternalSystemException
	 */
	UserLoginResponse createUserLoginResponse(String username,
			UserSession userSession) throws InternalSystemException;

}
