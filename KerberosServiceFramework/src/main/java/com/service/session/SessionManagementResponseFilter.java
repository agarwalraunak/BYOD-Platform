package com.service.session;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.app.rest.representation.AppAuthenticationResponse;
import com.service.app.rest.representation.UserAccessServiceResponse;
import com.service.app.rest.representation.UserServiceAuthenticationResponse;
import com.service.model.SessionDirectory;
import com.service.model.app.AppSession;
import com.service.model.app.UserSession;
import com.service.service.rest.representation.AppAccessServiceResponse;
import com.service.session.SessionManagementAPIImpl.RequestParam;
import com.service.util.dateutil.IDateUtil;
import com.service.util.encryption.IEncryptionUtil;
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
		
		Object unknownRestResponse = response.getEntity();
		if (unknownRestResponse instanceof AppAuthenticationResponse){
			AppAuthenticationResponse appAuthResponse = (AppAuthenticationResponse) unknownRestResponse;
			response.setEntity(appAuthResponse);
		}
		
		else if (unknownRestResponse instanceof UserServiceAuthenticationResponse){
			
			UserServiceAuthenticationResponse userAuthResponse = (UserServiceAuthenticationResponse)response.getEntity();
			
			AppSession appSession = (AppSession)httpRequest.getAttribute(RequestParam.APP_SESSION.getValue()); 
			String requestAuthenticatorStr = (String)httpRequest.getAttribute(RequestParam.REQUEST_AUTHENTICATOR.getValue());
			
			Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
			SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSession.getSessionID());
			
			Date responseAuthenticator = iDateUtil.createResponseAuthenticator(requestAuthenticator);
			String responseAuthenticatorStr = iDateUtil.generateStringFromDate(responseAuthenticator);
			
			String userSessionID = userAuthResponse.getEncUserSessionID();
			
			String encData[] = iEncryptionUtil.encrypt(appSessionKey, responseAuthenticatorStr, userSessionID, userAuthResponse.getEncExpiryTime());
			
			userAuthResponse.setEncResponseAuthenticator(encData[0]);
			userAuthResponse.setEncUserSessionID(encData[1]);
			userAuthResponse.setEncExpiryTime(encData[2]);
			
			response.setEntity(userAuthResponse);
			
		}
		else if (unknownRestResponse instanceof AppAccessServiceResponse){
			
			AppAccessServiceResponse accessResponse = (AppAccessServiceResponse) response.getEntity();
			
			AppSession appSession = (AppSession)httpRequest.getAttribute(RequestParam.APP_SESSION.getValue());
			String requestAuthenticatorStr = (String)httpRequest.getAttribute(RequestParam.REQUEST_AUTHENTICATOR.getValue());
			
			SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSession.getSessionID());
			Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
			
			Date responseAuthenticator = iDateUtil.createResponseAuthenticator(requestAuthenticator);
			String responseAuthenticatorStr = iDateUtil.generateStringFromDate(responseAuthenticator);
			
			String encResponseAuthenticator = iEncryptionUtil.encrypt(appSessionKey, responseAuthenticatorStr)[0];
			Map<String, String> encData = iEncryptionUtil.encrypt(appSessionKey, accessResponse.getEncResponseData());
			
			accessResponse.setEncResponseData(encData);;
			accessResponse.setEncResponseAuthenticator(encResponseAuthenticator);;
			
			response.setEntity(accessResponse);
		}
		else if (unknownRestResponse instanceof UserAccessServiceResponse){
		
			UserAccessServiceResponse accessResponse = (UserAccessServiceResponse) response.getEntity();
			
			UserSession userSession = (UserSession)httpRequest.getAttribute(RequestParam.USER_SESSION.getValue());
			String requestAuthenticatorStr = (String)httpRequest.getAttribute(RequestParam.REQUEST_AUTHENTICATOR.getValue());
			
			Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
			SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(userSession.getSessionID());
			
			Date responseAuthenticator = iDateUtil.createResponseAuthenticator(requestAuthenticator);
			String responseAuthenticatorStr = iDateUtil.generateStringFromDate(responseAuthenticator);
			
			String encResponseAuthenticator = iEncryptionUtil.encrypt(appSessionKey, responseAuthenticatorStr)[0];
			Map<String, String> encData = iEncryptionUtil.encrypt(appSessionKey, accessResponse.getData());
			
			accessResponse.setData(encData);
			accessResponse.setEncAuthenticator(encResponseAuthenticator);
			
			response.setEntity(accessResponse);
		}
		
		
		return response;
	}
	
	

}
