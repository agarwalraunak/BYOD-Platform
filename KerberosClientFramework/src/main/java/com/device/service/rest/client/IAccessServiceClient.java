/**
 * 
 */
package com.device.service.rest.client;

import java.io.IOException;
import java.util.Map;

import com.device.exception.InvalidResponseAuthenticatorException;
import com.device.exception.ResponseDecryptionException;
import com.device.exception.RestClientException;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;
import com.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

/**
 * This interface provides a secure channel between the <strong>App</strong> and <strong>Service</strong>
 * once, the App, User and Service have been mutually authenticated
 * 
 * @author raunak
 *
 */
public interface IAccessServiceClient {

	/**
	 * This method provides a secure channel between the <strong>App</strong> and <strong>Service</strong>.
	 * It requires <strong>Kerberos Service Ticket</strong> and
	 *  <strong>Application and User Authentication against the Service</strong> have been performed
	 * @param url
	 * <code>String</code> Web Service URL to be called for the service to be accessed
	 * @param requestMethod
	 * <code>RequestMethod</code> Request Method to be used for making the connection
	 * @param contentType
	 * <code>ContentType</code> Content Type header to be used in the request
	 * @param appSession
	 * <code>AppSession</code> App Session created by the Service being accessed
	 * @param kerberosServiceSessionID
	 * <code>String</code> Kerberos Generated Service Session ID from the App and Service
	 * @param userSession
	 * <code>UserSession</code> User Session created by the Service being invoked on Authentication
	 * @param data
	 * <code>Map<String, String></code> Data to be send on the Service Side
	 * @return
	 * <code>Map<String,String></code> Data sent by the Service
	 * @throws IOException
	 * In case there are some errors encountered while retrieving information
	 * @throws RestClientException
	 * If the status of the response is not <strong>200</strong>. The server side error message and error 
	 * response code can be accessed using <code>getMessage</code> and <code>getErrorCode</code> methods respectively
	 * @throws ResponseDecryptionException
	 * If the Response could not be decrypted
	 * @throws InvalidResponseAuthenticatorException
	 * If the Response Authenticator failed to validate
	 */
	Map<String, String> accessService(String url, RequestMethod requestMethod,
			ContentType contentType, AppSession appSession,
			String kerberosServiceSessionID, UserSession userSession,
			Map<String, String> data) throws IOException, RestClientException,
			ResponseDecryptionException,
			InvalidResponseAuthenticatorException;

}
