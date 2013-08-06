/**
 * 
 */
package com.kerberos.device.rest.api;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import com.kerberos.device.model.AppServiceSession;
import com.kerberos.device.model.ServiceTicket;
import com.kerberos.device.model.UserServiceSession;
import com.kerberos.device.rest.exceptions.UnauthorizedResponseException;
import com.kerberos.device.rest.representation.kerberos.service.AccessServiceResponse;
import com.kerberos.device.rest.representation.kerberos.service.AppAuthenticationRequest;
import com.kerberos.device.rest.representation.kerberos.service.UserLoginRequest;
import com.kerberos.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.kerberos.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

/**
 * @author raunak
 *
 */
public interface IClientAccessServiceAPI {


	/**
	 * @param serviceSessionKey
	 * @param serviceTicketPacket
	 * @param requestAuthenticator
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	AppAuthenticationRequest createAppAuthenticationRequest(
			SecretKey serviceSessionKey, String serviceTicketPacket,
			String requestAuthenticator) throws InvalidAttributeValueException;

	/**
	 * @param encAppSessionID
	 * @param encResponseAuthenticator
	 * @param requestAuthenticator
	 * @param serviceTicket
	 * @param serviceSessionKey
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	AppServiceSession processAuthenticateAppResponse(String encAppSessionID,
			String encResponseAuthenticator, Date requestAuthenticator,
			ServiceTicket serviceTicket, SecretKey serviceSessionKey)
			throws InvalidAttributeValueException;

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
	 * @param appServiceSession
	 * @param requestAuthenticator
	 * @param appSessionIDKey
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	UserServiceSession processUserLoginResponse(String encUsername,
			String encUserSessionID, String encResponseAuthenticator,
			AppServiceSession appServiceSession, Date requestAuthenticator,
			SecretKey appSessionIDKey) throws InvalidAttributeValueException;

	/**
	 * @param url
	 * @param requestMethod
	 * @param contentType
	 * @param appID
	 * @param appSessionID
	 * @param requestAuthenticator
	 * @param userServiceSessionID
	 * @param data
	 * @param kerberosAppServiceSessionKey
	 * @param appSessionKey
	 * @param userSessionKey
	 * @return
	 * @throws InvalidAttributeValueException
	 * @throws IOException 
	 */
	AccessServiceResponse generateAccessRequest(String url,
			RequestMethod requestMethod, ContentType contentType, String appID,
			String appSessionID, String requestAuthenticator,
			String userServiceSessionID, Map<String, String> data,
			SecretKey kerberosAppServiceSessionKey, SecretKey appSessionKey,
			SecretKey userSessionKey) throws InvalidAttributeValueException, IOException;

	/**
	 * @param requestAuthenticatorStr
	 * @param encResponseAuthenticator
	 * @param encData
	 * @param appServiceSession
	 * @param userServiceSession
	 * @param userSessionKey
	 * @return
	 * @throws InvalidAttributeValueException
	 * @throws UnauthorizedResponseException 
	 */
	Map<String, String> processAccessResponse(String requestAuthenticatorStr,
			String encResponseAuthenticator, Map<String, String> encData,
			AppServiceSession appServiceSession,
			UserServiceSession userServiceSession, SecretKey userSessionKey)
			throws InvalidAttributeValueException, UnauthorizedResponseException;

}
