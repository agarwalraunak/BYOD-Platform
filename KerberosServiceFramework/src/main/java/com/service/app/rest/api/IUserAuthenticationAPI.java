/**
 * 
 */
package com.service.app.rest.api;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import com.service.app.rest.representation.UserServiceAuthenticationRequest;
import com.service.app.rest.representation.UserServiceAuthenticationResponse;
import com.service.model.app.AppSession;
import com.service.rest.exception.common.AuthenticatorValidationException;
import com.service.rest.exception.common.InvalidRequestException;

/**
 * @author raunak
 *
 */
public interface IUserAuthenticationAPI {

	/**
	 * @param appSession
	 * @param appSessionKey
	 * @param request
	 * @return String[] of the decrypted request parameter or null either if the request is not valid 
	 * @throws AuthenticatorValidationException 
	 * @throws InvalidRequestException 
	 */
	String[] decrytAndValidateUserServiceAuthenticationRequestParameters(
			AppSession appSession, SecretKey appSessionKey,
			UserServiceAuthenticationRequest request) throws AuthenticatorValidationException, InvalidRequestException;

	/**
	 * @param requestAuthenticator
	 * @param userSessionID
	 * @param appSessionKey
	 * @param appSession
	 * @return
	 * @throws InvalidAttributeValueException 
	 */
	UserServiceAuthenticationResponse generateUserServiceAuthenticationResponse(
			Date requestAuthenticator, String userSessionID,
			SecretKey appSessionKey, AppSession appSession) ;
		
	
}
