/**
 * 
 */
package com.device.service.rest.api;

import java.io.IOException;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import com.device.rest.exceptions.UnauthorizedResponseException;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;
import com.device.service.rest.representation.AccessServiceResponse;
import com.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

/**
 * @author raunak
 *
 */
public interface IAccessServiceAPI {

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
	 * @param appSession
	 * @param userSession
	 * @param userSessionKey
	 * @return
	 * @throws InvalidAttributeValueException
	 * @throws UnauthorizedResponseException 
	 */
	Map<String, String> processAccessResponse(String requestAuthenticatorStr,
			String encResponseAuthenticator, Map<String, String> encData,
			AppSession appSession,
			UserSession userSession, SecretKey userSessionKey)
			throws InvalidAttributeValueException, UnauthorizedResponseException;

}
