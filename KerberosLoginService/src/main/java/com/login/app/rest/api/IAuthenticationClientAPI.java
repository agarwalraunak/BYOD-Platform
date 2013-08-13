/**
 * 
 */
package com.login.app.rest.api;

import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import com.login.app.rest.representation.AppAuthenticationResponse;
import com.login.app.rest.representation.UserLoginResponse;
import com.login.rest.exceptions.InvalidInputException;

/**
 * @author raunak
 *
 */
public interface IAuthenticationClientAPI {

	/**
	 * @param serviceTicketPacket
	 * @param encAuthenticator
	 * @param serviceKey
	 * @param responseData
	 * @return
	 * @throws InvalidAttributeValueException
	 * @throws InvalidInputException
	 */
	AppAuthenticationResponse processAppAuthenticationRequest(
			String serviceTicketPacket, String encAuthenticator,
			SecretKey serviceKey, Map<String, String> responseData)
			throws InvalidAttributeValueException, InvalidInputException;

	/**
	 * @param encUsername
	 * @param encPassword
	 * @param encAppSessionID
	 * @param encRequestAuthenticator
	 * @param appUsername
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	UserLoginResponse processUserLoginRequest(String encUsername, String encPassword,
			String encAppSessionID, String encRequestAuthenticator,
			String appUsername) throws InvalidAttributeValueException;

}
