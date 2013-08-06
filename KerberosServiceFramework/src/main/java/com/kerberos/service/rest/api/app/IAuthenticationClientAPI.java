/**
 * 
 */
package com.kerberos.service.rest.api.app;

import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import com.kerberos.rest.representation.device.AppAuthenticationResponse;
import com.kerberos.rest.representation.device.UserLoginResponse;
import com.kerberos.service.rest.exceptions.InvalidInputException;

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
