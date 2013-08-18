/**
 * 
 */
package com.service.session;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.app.rest.representation.AppAuthenticationRequest;
import com.service.app.rest.representation.UserAccessServiceRequest;
import com.service.app.rest.representation.UserServiceAuthenticationRequest;
import com.service.exception.common.AppSessionExpiredException;
import com.service.exception.common.AuthenticatorValidationException;
import com.service.exception.common.InternalSystemException;
import com.service.exception.common.InvalidRequestException;
import com.service.exception.common.UnauthenticatedAppException;
import com.service.exception.common.UnauthenticatedUserException;
import com.service.exception.common.UserSessionExpiredException;
import com.service.model.SessionDirectory;
import com.service.model.app.AppSession;
import com.service.model.app.UserSession;
import com.service.service.rest.representation.AppAccessServiceRequest;
import com.service.util.connectionmanager.IConnectionManager;
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
		
		String validatedRequestString = null;
		Object unknownRestRequest;
		
		if ((unknownRestRequest = iSessionManagementAPI.identifyRequest(entityString, AppAuthenticationRequest.class)) != null){
			AppAuthenticationRequest restRequest = (AppAuthenticationRequest)unknownRestRequest;
			validatedRequestString = entityString;
		}
		
		else if ((unknownRestRequest = iSessionManagementAPI.identifyRequest(entityString, UserServiceAuthenticationRequest.class)) != null){
			//Extract the Entity Object from the Entity String 
			UserServiceAuthenticationRequest userAuthRequest = (UserServiceAuthenticationRequest) unknownRestRequest;
			UserServiceAuthenticationRequest validatedRequest = null;
			try {
				validatedRequest = iSessionManagementAPI.validateUserServiceAuthenticationRequest(userAuthRequest);
			} catch (UnauthenticatedAppException | AuthenticatorValidationException e) {
				throw iSessionManagementAPI.createWebApplicationException(e);
			}
			
			//Valdiate the App Session and record the enteries
			AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(validatedRequest.getAppID());
			try {
				iSessionManagementAPI.manageAppSession(appSession, request.getPath(), validatedRequest.getEncAuthenticator(), httpRequest.getRemoteAddr());
			} catch (AppSessionExpiredException | UserSessionExpiredException e) {
				throw iSessionManagementAPI.createWebApplicationException(e);
			}
			
			validatedRequestString = iConnectionManager.generateJSONStringForObject(validatedRequest);
			
			//Set Http Request 
			iSessionManagementAPI.addAttributesToRequest(httpRequest, validatedRequest);
		}
		else if ((unknownRestRequest = iSessionManagementAPI.identifyRequest(entityString, AppAccessServiceRequest.class)) != null){
			
			AppAccessServiceRequest accessRequest = (AppAccessServiceRequest) unknownRestRequest;
			AppAccessServiceRequest validatedRequest = null;
			try {
				validatedRequest = iSessionManagementAPI.validateAppAccessServiceRequest(accessRequest);
			} catch (UnauthenticatedAppException | UnauthenticatedUserException
					| AuthenticatorValidationException e) {
				throw iSessionManagementAPI.createWebApplicationException(e);
			}
			
			AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(validatedRequest.getAppID());
			//Valdiate the App and User Session and record the enteries
			try {
				iSessionManagementAPI.manageAppSession(appSession, request.getPath(), validatedRequest.getEncAuthenticator(), httpRequest.getRemoteAddr());
			} catch (AppSessionExpiredException | UserSessionExpiredException e) {
				throw iSessionManagementAPI.createWebApplicationException(e);
			}

			validatedRequestString = iConnectionManager.generateJSONStringForObject(validatedRequest);
			
			//Set Http Request Attributes
			iSessionManagementAPI.addAttributesToRequest(httpRequest, validatedRequest);
		}
		else if ((unknownRestRequest = iSessionManagementAPI.identifyRequest(entityString, UserAccessServiceRequest.class)) != null){
			
			UserAccessServiceRequest accessRequest = (UserAccessServiceRequest) unknownRestRequest;
			UserAccessServiceRequest validatedRequest = null;
			try {
				validatedRequest = iSessionManagementAPI.validateUserAccessServiceRequest(accessRequest);
			} catch (UnauthenticatedAppException | UnauthenticatedUserException
					| AuthenticatorValidationException e) {
				throw iSessionManagementAPI.createWebApplicationException(e);
			}
			
			AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(validatedRequest.getAppID());
			UserSession userSession = appSession.findActiveUserSessionBySessionID(validatedRequest.getEncUserSessionID());
			//Valdiate the App and User Session and record the enteries
			try {
				iSessionManagementAPI.manageAppSession(appSession, request.getPath(), validatedRequest.getEncAuthenticator(), httpRequest.getRemoteAddr());
				iSessionManagementAPI.manageUserSession(userSession, request.getPath(), validatedRequest.getEncAuthenticator(), httpRequest.getRemoteAddr());
			} catch (AppSessionExpiredException | UserSessionExpiredException e) {
				throw iSessionManagementAPI.createWebApplicationException(e);
			}

			validatedRequestString = iConnectionManager.generateJSONStringForObject(validatedRequest);
			
			//Set Http Request Attributes
			iSessionManagementAPI.addAttributesToRequest(httpRequest, validatedRequest);
		}
		else{
			iSessionManagementAPI.createWebApplicationException(new InvalidRequestException());
		}
		
		
		//If the Validated Requset String is null or empty throw Exception
		if (validatedRequestString == null || validatedRequestString.isEmpty()){
			throw iSessionManagementAPI.createWebApplicationException(new InternalSystemException());
		}
		
		try {
			request.setEntityInputStream(new ByteArrayInputStream(validatedRequestString.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			throw iSessionManagementAPI.createWebApplicationException(new InternalSystemException());
		}
		
		
		return request;
	}
}