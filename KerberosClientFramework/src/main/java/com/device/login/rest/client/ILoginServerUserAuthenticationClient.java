package com.device.login.rest.client;

import java.io.IOException;

import com.device.exception.InvalidResponseAuthenticatorException;
import com.device.exception.RestClientException;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;

/**
 * This interface gets the <strong>User</strong> Authenticated against the <strong>Login Server</strong>
 * 
 * @author raunak
 *
 */
public interface ILoginServerUserAuthenticationClient {

	/**
	 * Authenticates the <strong>User</strong> against the <strong>Login Server</strong>. It 
	 * requires the mutual authentication of the <strong>App</strong> and <strong>Service</strong>
	 * performed first. 
	 * @param url
	 * <code>String</code> URL of the Login Server web service responsible to authenticate the User
	 * @param appSession
	 * <code>AppSession</code> App Session created by the Service for the App on authentication
	 * @param serviceSessionID
	 * <code>String</code> Kerberos generated Service Session ID for the App and Login Server
	 * @param username
	 * <code>String</code> Username of the user to be authenticated
	 * @param password
	 * <code>String</code> Password of the user to be authenticated
	 * @return
	 * <code>UserSession</code> object if the user is authenticated successfully else <code>null</code> 
	 * @throws IOException
	 * In case there are some errors encountered while retrieving information
	 * @throws RestClientException
	 * If the status of the response is not <strong>200</strong>. The server side error message and error 
	 * response code can be accessed using <code>getMessage</code> and <code>getErrorCode</code> methods respectively
	 * @throws InvalidResponseAuthenticatorException
	 * If the Response Authenticator fails to validate
	 */
	UserSession authenticateUser(String url, AppSession appSession,
			String serviceSessionID, String username, String password)
			throws IOException, RestClientException,
			InvalidResponseAuthenticatorException;

}
