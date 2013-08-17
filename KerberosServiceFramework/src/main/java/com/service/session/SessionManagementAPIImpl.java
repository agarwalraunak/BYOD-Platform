package com.service.session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.app.rest.representation.UserAccessServiceRequest;
import com.service.app.rest.representation.UserServiceAuthenticationRequest;
import com.service.error.ErrorResponse;
import com.service.exception.RestException;
import com.service.exception.common.AppSessionExpiredException;
import com.service.exception.common.AuthenticatorValidationException;
import com.service.exception.common.UnauthenticatedAppException;
import com.service.exception.common.UnauthenticatedUserException;
import com.service.exception.common.UserSessionExpiredException;
import com.service.model.SessionDirectory;
import com.service.model.app.AppSession;
import com.service.model.app.ClientSession;
import com.service.model.app.Request;
import com.service.model.app.UserSession;
import com.service.util.dateutil.IDateUtil;
import com.service.util.encryption.IEncryptionUtil;

@Component
public class SessionManagementAPIImpl implements ISessionManagementAPI {
	
	private @Autowired SessionDirectory sessionDirectory;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	
	public enum RequestParam{
		REQUEST_AUTHENTICATOR("REQUEST_AUTHENTICATOR"), APP_SESSION("APP_SESSION"), USER_SESSION("USER_SESSION");
		private String value;
		
		RequestParam(String value){
			this.value = value;
		}
		
		public String getValue(){
			return value;
		}
	}
	
	@Override
	public String getRequestEntityString(InputStream inputStream) throws IOException {
		
		if (inputStream == null){
			return null;
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String entity = reader.readLine();
		StringBuilder builder = new StringBuilder();
		while(entity!=null){
			builder.append(entity);
			entity = reader.readLine();
		}

		return builder.toString();
	}

	@Override
	public <T> T identifyRequest(String restServiceRequest, Class<T> clazz) {
		
		if (restServiceRequest == null || restServiceRequest.isEmpty() || clazz == null){
			return null;
		}
		
		ObjectMapper obj = new ObjectMapper();
		T request = null;
		try {
			request = obj.convertValue(obj.readValue(new StringReader(restServiceRequest), HashMap.class), clazz);
		} catch (IllegalArgumentException | IOException e) {}
		return request;
	}
	
	@Override
	public <T> T identifyResponse(String restServiceResponse, Class<T> clazz) {
		
		if (restServiceResponse == null || restServiceResponse.isEmpty() || clazz == null){
			return null;
		}
		
		ObjectMapper obj = new ObjectMapper();
		T request = null;
		try {
			request = obj.convertValue(obj.readValue(new StringReader(restServiceResponse), HashMap.class), clazz);
		} catch (IllegalArgumentException | IOException e) {}
		return request;
	}
	
	@Override
	public UserServiceAuthenticationRequest validateUserServiceAuthenticationRequest(UserServiceAuthenticationRequest request) throws UnauthenticatedAppException, AuthenticatorValidationException{
		
		String appLoginName = request.getAppID();
		
		AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(appLoginName);
		if (appSession == null){
			throw new UnauthenticatedAppException();
		}
		
		String encRequestAuthenticator = request.getEncAuthenticator();
		String encUserSessionID = request.getEncUserSessionID();
		String encUsername = request.getEncUsername();
		
		//Decrypt the request information
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSession.getSessionID());
		String[] decryptedData = iEncryptionUtil.decrypt(appSessionKey, encRequestAuthenticator, encUserSessionID, encUsername);
		
		//Validate the decrypted attributes
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			throw new UnauthenticatedAppException();
		}
		
		//Validate the authenticator
		String requestAuthenticatorStr = decryptedData[0];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		if (!appSession.validateAuthenticator(requestAuthenticator)){
			throw new AuthenticatorValidationException();
		}
		
		//setting the decrypted data in the request
		request.setEncAuthenticator(requestAuthenticatorStr);
		request.setEncUserSessionID(decryptedData[1]);
		request.setEncUsername(decryptedData[2]);
		
		return request;
	}
	
	@Override
	public UserAccessServiceRequest validateAccessServiceRequest(UserAccessServiceRequest request) throws UnauthenticatedAppException, UnauthenticatedUserException, AuthenticatorValidationException{
		
		AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(request.getAppID());
		if (appSession == null){
			throw new UnauthenticatedAppException();
		}
		
		String encAppSessionID = request.getEncAppSessionID();		
		//Decrypting the AppSessionID
		SecretKey serviceSessionKey = iEncryptionUtil.generateSecretKey(appSession.getKerberosServiceSessionID());
		String decAppSessionID = iEncryptionUtil.decrypt(serviceSessionKey, encAppSessionID)[0];		
		
		//Validate the decrypted App Session ID
		if (decAppSessionID == null || decAppSessionID.isEmpty() || !decAppSessionID.equals(appSession.getSessionID())){
			throw new UnauthenticatedAppException();
		}

		String encRequestAuthenticator = request.getEncAuthenticator();
		String encUserSessionID = request.getEncUserSessionID();
		//Decrypt the request information
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSession.getSessionID());
		String[] decryptedData = iEncryptionUtil.decrypt(appSessionKey, encRequestAuthenticator, encUserSessionID);
		
		//Validate the decrypted attributes
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			throw new UnauthenticatedAppException();
		}
		
		//Validate the authenticator
		String requestAuthenticatorStr = decryptedData[0];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		if (!appSession.validateAuthenticator(requestAuthenticator)){
			throw new AuthenticatorValidationException();
		}
		
		//Validate the Decrypted User SessionID
		String decUserSessionID = decryptedData[1];
		UserSession userSession = appSession.findActiveUserSessionBySessionID(decUserSessionID);
		if (userSession == null){
			throw new UnauthenticatedUserException();
		}
		
		//Finally decrypting the data
		Map<String, String> encData = request.getData();
		SecretKey userSessionKey = iEncryptionUtil.generateSecretKey(decUserSessionID);
		Map<String, String> decData = iEncryptionUtil.decrypt(userSessionKey, encData);
		
		//Setting the decrypted information in the request
		request.setData(decData);
		request.setEncAppSessionID(decAppSessionID);
		request.setEncAuthenticator(requestAuthenticatorStr);
		request.setEncUserSessionID(decUserSessionID);

		return request;
	}
	
	
	@Override
	public HttpServletRequest addAttributesToRequest(HttpServletRequest httpRequest, Object entity){
		
		if (entity == null || httpRequest == null){
			return null;
		}
		
		if (entity instanceof UserServiceAuthenticationRequest){
			UserServiceAuthenticationRequest validatedRequest = (UserServiceAuthenticationRequest) entity;
			httpRequest.setAttribute(RequestParam.REQUEST_AUTHENTICATOR.getValue(), validatedRequest.getEncAuthenticator());
			httpRequest.setAttribute(RequestParam.APP_SESSION.getValue(), sessionDirectory.findActiveAppSessionByAppID(validatedRequest.getAppID()));
		}
		
		if (entity instanceof UserAccessServiceRequest){
			UserAccessServiceRequest validatedRequest = (UserAccessServiceRequest) entity;
			AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(validatedRequest.getAppID());
			UserSession userSession = appSession.findActiveUserSessionBySessionID(validatedRequest.getEncUserSessionID());
			httpRequest.setAttribute(RequestParam.REQUEST_AUTHENTICATOR.getValue(), validatedRequest.getEncAuthenticator());
			httpRequest.setAttribute(RequestParam.APP_SESSION.getValue(), appSession);
			httpRequest.setAttribute(RequestParam.USER_SESSION.getValue(), userSession);
		}

		return httpRequest;
	}
	
	@Override
	public boolean manageAppSession(AppSession session, String path, String requestAuthenticator, String clientIP) throws AppSessionExpiredException, UserSessionExpiredException{
		return manageSession(session, path, requestAuthenticator, clientIP);
	}
	
	@Override
	public boolean manageUserSession(UserSession session, String path, String requestAuthenticator, String clientIP) throws AppSessionExpiredException, UserSessionExpiredException{
		return manageSession(session, path, requestAuthenticator, clientIP);
	}
	
	public boolean manageSession(ClientSession session, String path, String requestAuthenticator, String clientIP) throws AppSessionExpiredException, UserSessionExpiredException{
		
		Request request = new Request();
		request.setPath(path);
		request.setRequestAuthenticator(requestAuthenticator);
		
		session.addRequest(request);
		
		//Check if the Request IP Address is the same for which the session was created
		//Check if the Session has not been expired
		if (!session.getClientIP().equals(clientIP)){
			return false;
		}
		
		if (session.getExpiryTime().before(new Date())){
			if (session instanceof AppSession){
				throw new AppSessionExpiredException();
			}
			else if (session instanceof UserSession){
				throw new UserSessionExpiredException();
			}
			session.setActive(false);
		}
		return true;
	}
	
	@Override
	public WebApplicationException createWebApplicationException(RestException exception){
		
		if (exception == null){
			return null;
		}
		ErrorResponse response = new ErrorResponse();
		response.setErrorId(exception.getErrorID());
		response.setErrorMessage(exception.getMessage());
	
		return new WebApplicationException(Response.status(response.getErrorId()).entity(response).type(MediaType.APPLICATION_JSON).build());
	}
}