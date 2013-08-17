/**
 * 
 */
package com.login.session;

import java.io.IOException;
import java.io.InputStream;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import com.login.exception.RestException;
import com.login.exception.common.AppSessionExpiredException;
import com.login.exception.common.AuthenticatorValidationException;
import com.login.exception.common.IPChangeException;
import com.login.exception.common.InternalSystemException;
import com.login.exception.common.InvalidRequestException;
import com.login.exception.common.UnauthenticatedAppException;
import com.login.exception.common.UnauthenticatedUserException;
import com.login.exception.common.UserSessionExpiredException;
import com.login.model.app.AppSession;
import com.login.model.app.UserSession;
import com.login.rest.representation.RestServiceRequest;
import com.login.rest.representation.RestServiceResponse;

/**
 * This interface provides the required methods to perform session management
 * 
 * @author raunak
 *
 */
public interface ISessionManagementAPI {
	
	/**
	 * @param inputStream
	 * @return
	 * @throws IOException 
	 */
	public String getRequestEntityString(InputStream inputStream) throws IOException;
	

	/**
	 * @param restServiceRequest
	 * @param clazz
	 * @return
	 */
	<T> T identifyRequest(String restServiceRequest, Class<T> clazz);
	

	/**
	 * @param session
	 * @param path
	 * @param requestAuthenticator
	 * @param clientIP
	 * @return
	 * @throws UserSessionExpiredException 
	 * @throws AppSessionExpiredException 
	 * @throws IPChangeException 
	 */
	boolean manageAppSession(AppSession session, String path,
			String requestAuthenticator, String clientIP) throws AppSessionExpiredException, UserSessionExpiredException, IPChangeException;


	/**
	 * @param session
	 * @param path
	 * @param requestAuthenticator
	 * @param clientIP
	 * @return
	 * @throws UserSessionExpiredException 
	 * @throws AppSessionExpiredException 
	 * @throws IPChangeException 
	 */
	boolean manageUserSession(UserSession session, String path,
			String requestAuthenticator, String clientIP) throws AppSessionExpiredException, UserSessionExpiredException, IPChangeException;

	/**
	 * @param exception
	 * @return
	 */
	WebApplicationException createWebApplicationException(
			RestException exception);


	/**
	 * @param restServiceResponse
	 * @param clazz
	 * @return
	 */
	<T> T identifyResponse(String restServiceResponse, Class<T> clazz);


	/**
	 * @param httpRequest
	 * @param restRequest
	 * @return
	 */
	HttpServletRequest setAttributesToRequest(HttpServletRequest httpRequest,
			RestServiceRequest restRequest);


	/**
	 * @param requestEntityString
	 * @return
	 * @throws UnauthenticatedAppException
	 * @throws AuthenticatorValidationException
	 * @throws UnauthenticatedUserException
	 * @throws InvalidRequestException
	 */
	<T extends RestServiceRequest> T validateRequest(String requestEntityString)
			throws UnauthenticatedAppException,
			AuthenticatorValidationException, UnauthenticatedUserException,
			InvalidRequestException;


	/**
	 * @param response
	 * @param key
	 * @return
	 * @throws InternalSystemException
	 */
	RestServiceResponse encryptResponseData(RestServiceResponse response,
			SecretKey key) throws InternalSystemException;





}
