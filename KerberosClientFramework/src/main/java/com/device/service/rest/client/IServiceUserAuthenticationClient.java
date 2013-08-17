/**
 * 
 */
package com.device.service.rest.client;

import java.io.IOException;

import com.device.exception.ApplicationDetailServiceUninitializedException;
import com.device.exception.InvalidResponseAuthenticatorException;
import com.device.exception.RestClientException;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;

/**
 * The interface allows to get the <strong>User</strong> authenticated against the <strong>Service</strong>
 * 
 * @author raunak
 *
 */
public interface IServiceUserAuthenticationClient {


	/**
	 * This method does Service Authentication of the User. This method requires 
	 * <strong>App's Kerberos Authentication</strong> and <strong>User Login Service Authentication</strong>
	 * to be performed  
	 * @param serviceUserAuthenticationURL 
	 * <code>String</code> Web service URL to be called for User Service Authentication
	 * @param userLoginServiceSession
	 * <code>UserSession</code> User Login Service Session created by the <strong>LoginServer</strong>
	 * @param appSession 
	 * <code>AppSession</code> App Session for the service for which the user is being authenticated
	 * @return 
	 * <code>boolean</code> true if successfully authenticated else false
	 * @throws IOException
	 * In case there are some errors encountered while retrieving information
	 * @throws RestClientException 
	 * If the status of the response is not <strong>200</strong>. The server side error message and error 
	 * response code can be accessed using <code>getMessage</code> and <code>getErrorCode</code> methods respectively
	 * @throws InvalidResponseAuthenticatorException 
	 * If the Response Authenticator is not valid
	 * @throws ApplicationDetailServiceUninitializedException
	 * If the <code>ApplicationDetailService</code> has not been initialized properly 
	 */
	boolean serviceUserAuthentication(String serviceUserAuthenticationURL,
			UserSession userLoginServiceSession, AppSession appSession)
			throws IOException,
			RestClientException,
			InvalidResponseAuthenticatorException,
			ApplicationDetailServiceUninitializedException;

}
