/**
 * 
 */
package com.device.service.rest.api;

import java.io.IOException;
import java.util.Map;

import javax.crypto.SecretKey;

import com.device.exception.InvalidResponseAuthenticatorException;
import com.device.exception.ResponseDecryptionException;
import com.device.exception.RestClientException;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;
import com.device.service.rest.representation.UserAccessServiceResponse;
import com.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

/**
 * @author raunak
 *
 */
public interface IAccessServiceAPI {

	/**
	 * @param url: String url of the web service being accessed
	 * @param requestMethod: Request Method of the request
	 * @param contentType: Content Type of the request
	 * @param appID: String appLoginName
	 * @param appSessionID: String sessionID for the app created by the service being invoked
	 * @param requestAuthenticator: String Authenticator for the request
	 * @param userServiceSessionID: String sessionID for the user created by the service being invoked
	 * @param data: Map<String, String> data to be send with the request to the service
	 * @param kerberosAppServiceSessionKey: SecretKey made using the kerberos Service Session ID
	 * @param appSessionKey: SecretKey created using the AppSessionID
	 * @param userSessionKey: SecretKey created using the AppSessionID
	 * @return AccessServiceResponse or null if the input parameter are invalid
	 * @throws IOException 
	 * @throws RestClientException 
	 */
	UserAccessServiceResponse generateAccessRequest(String url,
			RequestMethod requestMethod, ContentType contentType, String appID,
			String appSessionID, String requestAuthenticator,
			String userServiceSessionID, Map<String, String> data,
			SecretKey kerberosAppServiceSessionKey, SecretKey appSessionKey,
			SecretKey userSessionKey) throws IOException, RestClientException;

	/**
	 * @param requestAuthenticatorStr: String request authenticator
	 * @param encResponseAuthenticator: String encrypted response authenticator
	 * @param encData: Map<String, String> encrypted response data
	 * @param appSession: AppSession for the service being invoked
	 * @param userSession: UserSession for the service being invoked
	 * @param userSessionKey: Secretkey made using the User Session ID
	 * @return Map<String, String> of decrypted data or null if input parameters are invalid. Returns the encData if the 
	 * input encData is empty or null 
	 * @throws ResponseDecryptionException 
	 * @throws InvalidResponseAuthenticatorException 
	 */
	Map<String, String> processAccessResponse(String requestAuthenticatorStr,
			String encResponseAuthenticator, Map<String, String> encData,
			AppSession appSession,
			UserSession userSession, SecretKey userSessionKey)
			throws ResponseDecryptionException, InvalidResponseAuthenticatorException;

}
