package com.login.session;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.app.rest.representation.AppAuthenticationResponse;
import com.login.exception.common.InternalSystemException;
import com.login.model.SessionDirectory;
import com.login.model.app.AppSession;
import com.login.model.app.Request;
import com.login.model.app.UserSession;
import com.login.rest.representation.RestServiceResponse;
import com.login.session.SessionManagementAPIImpl.RequestParam;
import com.login.util.dateutil.IDateUtil;
import com.login.util.encryption.IEncryptionUtil;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * @author raunak
 *
 */

@Component
public class SessionManagementResponseFilter implements ContainerResponseFilter {
	
	private @Autowired ISessionManagementAPI iSessionManagementAPI;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	private @Autowired SessionDirectory sessionDirectory;
	
	@Context HttpServletRequest httpRequest;
	
	@Override
	public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
		
		Object unknownResponse = response.getEntity();
		AppSession appSession = (AppSession)httpRequest.getAttribute(RequestParam.APP_SESSION.getValue()); 
		String requestAuthenticatorStr = (String)httpRequest.getAttribute(RequestParam.REQUEST_AUTHENTICATOR.getValue());
		
		//Create the response authenticator
		Date requestAuthenticator = null;
		Date responseAuthenticator = null;
		if (requestAuthenticatorStr != null){
			requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
			responseAuthenticator = iDateUtil.createResponseAuthenticator(requestAuthenticator);
		}
		
		if (!(unknownResponse instanceof RestServiceResponse)){
			if (appSession != null){
				appSession.createRequest(request.getPath(), requestAuthenticator).createResponse(response.getStatus(), responseAuthenticator);;
			}
			response.setEntity(unknownResponse);
			return response;
		}

		if (unknownResponse instanceof AppAuthenticationResponse){
			response.setEntity(unknownResponse);
			return response;
		}
			
		RestServiceResponse restResponse = (RestServiceResponse)unknownResponse;
		if (appSession == null){
			throw iSessionManagementAPI.createWebApplicationException(new InternalSystemException());
		}
		
		//Setting the Response Authenticator
		restResponse.setEncResponseAuthenticator(iDateUtil.generateStringFromDate(responseAuthenticator));
		//Add the authenticators to the App Session
		appSession.addAuthenticator(requestAuthenticator);
		appSession.addAuthenticator(responseAuthenticator);
		
		//Recording the response in the App Session
		Request appModelRequest = appSession.getRequestList().get(appSession.getRequestList().size()-1);
		appModelRequest.createResponse(response.getStatus(), responseAuthenticator);
		
		SecretKey key;
		
		//User Session would not be null for UserAccessServiceRequest or failed UserLoginRequest 
		UserSession userSession = (UserSession)httpRequest.getAttribute(RequestParam.USER_SESSION.getValue());
		if (userSession != null){
			//Add the Authenticators to User Session
			userSession.addAuthenticator(requestAuthenticator);
			userSession.addAuthenticator(responseAuthenticator);
			//Recording the Response in the User Session
			Request userModelRequest = userSession.getRequestList().get(userSession.getRequestList().size()-1);
			userModelRequest.createResponse(response.getStatus(), responseAuthenticator);

			key = iEncryptionUtil.generateSecretKey(userSession.getSessionID());
		}
		else{
			key = iEncryptionUtil.generateSecretKey(appSession.getSessionID());
		}
		
		//Encrypting the response data
		try {
			iSessionManagementAPI.encryptResponseData(restResponse, key);
		} catch (InternalSystemException e) {
			e.printStackTrace();
			iSessionManagementAPI.createWebApplicationException(e);
		}
		
		response.setEntity(restResponse);
		
		return response;
	}
}
