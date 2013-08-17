/**
 * 
 */
package com.login.session;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.app.rest.representation.AppAuthenticationRequest;
import com.login.exception.common.AppSessionExpiredException;
import com.login.exception.common.AuthenticatorValidationException;
import com.login.exception.common.IPChangeException;
import com.login.exception.common.InternalSystemException;
import com.login.exception.common.InvalidRequestException;
import com.login.exception.common.UnauthenticatedAppException;
import com.login.exception.common.UnauthenticatedUserException;
import com.login.exception.common.UserSessionExpiredException;
import com.login.model.SessionDirectory;
import com.login.model.app.AppSession;
import com.login.rest.representation.AppRestServiceRequest;
import com.login.service.rest.representation.UserAccessServiceRequest;
import com.login.util.connectionmanager.IConnectionManager;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * @author raunak
 *
 */
@Component
public class SessionManagementRequestFilter implements  ContainerRequestFilter {
	
	private @Autowired ISessionManagementAPI iSessionManagementAPI;
	private @Autowired IConnectionManager iConnectionManager;
	private @Autowired SessionDirectory sessionDirectory;
	@Context HttpServletRequest httpRequest;
	@Context UriInfo uriInfo;
	
	@Override
	public ContainerRequest filter(ContainerRequest request) {
		
		
		//Extract the Entity String from the ContainerRequest
		InputStream inputStream = request.getEntityInputStream();
		String entityString = null;
		if (inputStream != null){
			try {
				entityString = iSessionManagementAPI.getRequestEntityString(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		AppAuthenticationRequest appAuthenticationRequest = iSessionManagementAPI.identifyRequest(entityString, AppAuthenticationRequest.class);
		String validatedRequestString = null;
		if (appAuthenticationRequest == null){
			AppRestServiceRequest restRequest;
			try {
				restRequest = iSessionManagementAPI.validateRequest(entityString);
				AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(restRequest.getAppID());
				iSessionManagementAPI.manageAppSession(appSession, request.getPath(), restRequest.getEncAuthenticator(), httpRequest.getRemoteAddr());
				if (restRequest instanceof UserAccessServiceRequest){
					UserAccessServiceRequest userAccessRequest = (UserAccessServiceRequest)restRequest;
					iSessionManagementAPI.manageUserSession(appSession.findActiveUserSessionBySessionID(userAccessRequest.getEncUserSessionID()), request.getPath(), restRequest.getEncAuthenticator(), httpRequest.getRemoteAddr());
				}
			} catch (UnauthenticatedAppException | AuthenticatorValidationException
					| UnauthenticatedUserException | InvalidRequestException | AppSessionExpiredException | UserSessionExpiredException | IPChangeException e1) {
				throw iSessionManagementAPI.createWebApplicationException(e1);
			}
			
			
			//Setting the attributes in the HttpServletRequest
			iSessionManagementAPI.setAttributesToRequest(httpRequest, restRequest);
			
			
			validatedRequestString = iConnectionManager.generateJSONStringForObject(restRequest);
			
			//If the Validated Requset String is null or empty throw Exception
			if (validatedRequestString == null || validatedRequestString.isEmpty()){
				throw iSessionManagementAPI.createWebApplicationException(new InternalSystemException());
			}
		}
		else{
			validatedRequestString = iConnectionManager.generateJSONStringForObject(appAuthenticationRequest);
		}

		try {
			request.setEntityInputStream(new ByteArrayInputStream(validatedRequestString.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw iSessionManagementAPI.createWebApplicationException(new InternalSystemException());
		}

		return request;	
	}
}