package com.login.session;

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

import com.login.app.rest.representation.UserLoginRequest;
import com.login.app.rest.representation.UserLoginResponse;
import com.login.error.ErrorResponse;
import com.login.exception.RestException;
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
import com.login.model.app.ClientSession;
import com.login.model.app.UserSession;
import com.login.rest.representation.AppRestServiceRequest;
import com.login.rest.representation.RestServiceRequest;
import com.login.rest.representation.RestServiceResponse;
import com.login.rest.representation.UserRestServiceRequest;
import com.login.service.rest.representation.AppAccessServiceRequest;
import com.login.service.rest.representation.AppAccessServiceResponse;
import com.login.service.rest.representation.ServiceValidateUserAuthenticationRequest;
import com.login.service.rest.representation.ServiceValidateUserAuthenticationResponse;
import com.login.service.rest.representation.UserAccessServiceRequest;
import com.login.service.rest.representation.UserAccessServiceResponse;
import com.login.util.dateutil.IDateUtil;
import com.login.util.encryption.IEncryptionUtil;

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
	public <T extends RestServiceRequest> T validateRequest(String requestEntityString) throws UnauthenticatedAppException, AuthenticatorValidationException, UnauthenticatedUserException, InvalidRequestException{
		
		if (requestEntityString == null || requestEntityString.isEmpty()){
			return null;
		}
		
		AppAccessServiceRequest appAccessServiceRequest = identifyRequest(requestEntityString, AppAccessServiceRequest.class);
		if (appAccessServiceRequest != null){
			return (T)validateAppAccessServiceRequest(appAccessServiceRequest);
		}
		
		UserLoginRequest userLoginRequest = identifyRequest(requestEntityString, UserLoginRequest.class);
		if (userLoginRequest != null){
			return (T) validateUserLoginRequest(userLoginRequest);
		}
		
		ServiceValidateUserAuthenticationRequest serviceUserAuthRequest = identifyRequest(requestEntityString, ServiceValidateUserAuthenticationRequest.class);
		if (serviceUserAuthRequest != null){
			return (T)validateServiceValidateUserAuthenticationRequest(serviceUserAuthRequest);
		}
		
		UserAccessServiceRequest userAccessServiceRequest = identifyRequest(requestEntityString, UserAccessServiceRequest.class);
		if (userAccessServiceRequest != null){
			return (T)validateUserAccessServiceRequest(userAccessServiceRequest);
		}
		
		throw new InvalidRequestException();
	}
	
	
	@Override
	public HttpServletRequest setAttributesToRequest(HttpServletRequest httpRequest, RestServiceRequest restRequest){
		
		if (restRequest == null || httpRequest == null){
			return null;
		}
		
		if (restRequest instanceof UserAccessServiceRequest){
			UserRestServiceRequest validatedRequest = (UserRestServiceRequest) restRequest;
			AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(validatedRequest.getAppID());
			UserSession userSession = appSession.findActiveUserSessionBySessionID(validatedRequest.getEncUserSessionID());
			httpRequest.setAttribute(RequestParam.REQUEST_AUTHENTICATOR.getValue(), validatedRequest.getEncAuthenticator());
			httpRequest.setAttribute(RequestParam.APP_SESSION.getValue(), appSession);
			httpRequest.setAttribute(RequestParam.USER_SESSION.getValue(), userSession);
		}
		
		if ((restRequest instanceof UserLoginRequest) || (restRequest instanceof AppAccessServiceRequest) || (restRequest instanceof ServiceValidateUserAuthenticationRequest)){
			AppRestServiceRequest validatedRequest = (AppRestServiceRequest) restRequest;
			httpRequest.setAttribute(RequestParam.REQUEST_AUTHENTICATOR.getValue(), validatedRequest.getEncAuthenticator());
			httpRequest.setAttribute(RequestParam.APP_SESSION.getValue(), sessionDirectory.findActiveAppSessionByAppID(validatedRequest.getAppID()));
		}

		return httpRequest;
	}
	
	@Override
	public boolean manageAppSession(AppSession session, String path, String requestAuthenticator, String clientIP) throws AppSessionExpiredException, UserSessionExpiredException, IPChangeException{
		return manageSession(session, path, requestAuthenticator, clientIP);
	}
	
	@Override
	public boolean manageUserSession(UserSession session, String path, String requestAuthenticator, String clientIP) throws AppSessionExpiredException, UserSessionExpiredException, IPChangeException{
		return manageSession(session, path, requestAuthenticator, clientIP);
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
	
	@Override
	public RestServiceResponse encryptResponseData(RestServiceResponse response, SecretKey key) throws InternalSystemException{
		
		response.setEncResponseAuthenticator(iEncryptionUtil.encrypt(key, response.getEncResponseAuthenticator())[0]);
		
		if (response instanceof UserLoginResponse){
			return encryptUserLoginResponseData((UserLoginResponse)response, key);
		}
		else if (response instanceof AppAccessServiceResponse){
			return encryptAppAccessServiceResponseData((AppAccessServiceResponse)response, key);
		}
		else if (response instanceof ServiceValidateUserAuthenticationResponse){
			return encryptServiceValidateUserAuthenticationResponseData((ServiceValidateUserAuthenticationResponse)response, key);
		}
		else if (response instanceof UserAccessServiceResponse){
			return encryptUserAccessServiceResponseData((UserAccessServiceResponse)response, key);
		}
		
		throw new InternalSystemException();
	}
	
	public UserLoginResponse encryptUserLoginResponseData(UserLoginResponse response, SecretKey appSessionKey){

		String[] encryptedData = iEncryptionUtil.encrypt(appSessionKey, response.getEncUsername(), response.getEncUserSessionID(), response.getEncExpiryTime());
		response.setEncUsername(encryptedData[0]);
		response.setEncUserSessionID(encryptedData[1]);
		response.setEncExpiryTime(encryptedData[2]);
		return response;
	}
	
	public AppAccessServiceResponse encryptAppAccessServiceResponseData(AppAccessServiceResponse response, SecretKey appSessionKey){
		
		response.setEncResponseData(iEncryptionUtil.encrypt(appSessionKey, response.getEncResponseData()));
		return response;
	}
	
	public ServiceValidateUserAuthenticationResponse encryptServiceValidateUserAuthenticationResponseData(ServiceValidateUserAuthenticationResponse response, SecretKey appSessionKey){
		
		response.setEncIsAuthenticated(iEncryptionUtil.encrypt(appSessionKey, response.getEncIsAuthenticated())[0]);
		return response;
	}
	
	public UserAccessServiceResponse encryptUserAccessServiceResponseData(UserAccessServiceResponse response, SecretKey userSessionKey){
		
		response.setData(iEncryptionUtil.encrypt(userSessionKey, response.getData()));
		return response;
	}
	
	
	public boolean manageSession(ClientSession session, String path, String requestAuthenticator, String clientIP) throws AppSessionExpiredException, UserSessionExpiredException, IPChangeException{
		
		session.createRequest(path, iDateUtil.generateDateFromString(requestAuthenticator));
		//Check if the Request IP Address is the same for which the session was created
		//Check if the Session has not been expired
		if (!session.getClientIP().equals(clientIP)){
			throw new IPChangeException();
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
	
	public AppRestServiceRequest validateAppRestServiceRequest(AppRestServiceRequest request) throws UnauthenticatedAppException, AuthenticatorValidationException{
		
		if (request == null){
			return null;
		}
		
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
		//Decrypt the request information
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSession.getSessionID());
		String requestAuthenticatorStr = iEncryptionUtil.decrypt(appSessionKey, encRequestAuthenticator)[0];
		
		//Validate the decrypted attributes
		if (!iEncryptionUtil.validateDecryptedAttributes(requestAuthenticatorStr)){
			throw new UnauthenticatedAppException();
		}
		
		//Validate the authenticator
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		if (!appSession.validateAuthenticator(requestAuthenticator)){
			throw new AuthenticatorValidationException();
		}
		
		//Setting the decrypted attributes in the request
		request.setEncAppSessionID(decAppSessionID);
		request.setEncAuthenticator(requestAuthenticatorStr);
		
		return request;
	}
	
	public UserRestServiceRequest validateUserRestServiceRequest(UserRestServiceRequest request) throws UnauthenticatedAppException, AuthenticatorValidationException, UnauthenticatedUserException{
		
		if (validateAppRestServiceRequest(request) == null){
			return null;
		}
		
		String appSessionID = request.getEncAppSessionID();
		String encUserSessionID = request.getEncUserSessionID();
		
		//Decrypting the User Session ID
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSessionID);
		String decUserSessionID = iEncryptionUtil.decrypt(appSessionKey, encUserSessionID)[0];
		//Validating the Decrypted Attribute
		if (decUserSessionID == null || decUserSessionID.isEmpty()){
			throw new UnauthenticatedAppException();
		}
		
		AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(appSessionID);
		
		UserSession userSession = appSession.findActiveUserSessionBySessionID(decUserSessionID);
		if (userSession == null){
			throw new UnauthenticatedUserException();
		}
		
		request.setEncUserSessionID(decUserSessionID);
		
		return request;
	}
	
	public UserAccessServiceRequest validateUserAccessServiceRequest(UserAccessServiceRequest request) throws UnauthenticatedAppException, AuthenticatorValidationException, UnauthenticatedUserException{
		
		if (request == null){
			return null;
		}
		
		validateUserRestServiceRequest(request);
		
		String userSessionID = request.getEncUserSessionID();
		Map<String, String> encData = request.getData();
		SecretKey userSessionKey = iEncryptionUtil.generateSecretKey(userSessionID);
		Map<String, String> decData = iEncryptionUtil.decrypt(userSessionKey, encData);
		request.setData(decData);
		
		return request;
		
	}
	
	public AppAccessServiceRequest validateAppAccessServiceRequest(AppAccessServiceRequest request) throws UnauthenticatedAppException, AuthenticatorValidationException{
		
		if (request == null){
			return null;
		}
		
		validateAppRestServiceRequest(request);
		
		String appSessionID = request.getEncAppSessionID();
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSessionID);
		
		Map<String, String> data = request.getData();
		request.setData(iEncryptionUtil.decrypt(appSessionKey, data));
		
		return request;
	}
	
	public UserLoginRequest validateUserLoginRequest(UserLoginRequest request) throws UnauthenticatedAppException, AuthenticatorValidationException{
		
		if (request == null){
			return request;
		}
		
		validateAppRestServiceRequest(request);
		
		String encUsername = request.getEncUsername();
		String encPassword = request.getEncPassword();
		
		String appSessionID = request.getEncAppSessionID();
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSessionID);
		
		String[] decryptedData = iEncryptionUtil.decrypt(appSessionKey, encUsername, encPassword);
		
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			throw new UnauthenticatedAppException();
		}
		
		request.setEncUsername(decryptedData[0]);
		request.setEncPassword(decryptedData[1]);
		
		return request;
	}
	
	public ServiceValidateUserAuthenticationRequest validateServiceValidateUserAuthenticationRequest(ServiceValidateUserAuthenticationRequest request) throws UnauthenticatedAppException, AuthenticatorValidationException{
		
		if (request == null){
			return request;
		}
		
		validateAppRestServiceRequest(request);
		
		String encUserLoginSessionID = request.getEncUserLoginSessionID();
		
		String appSessionID = request.getEncAppSessionID();
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSessionID);
		
		String decryptedUserSessionID = iEncryptionUtil.decrypt(appSessionKey, encUserLoginSessionID)[0];
		
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedUserSessionID)){
			throw new UnauthenticatedAppException();
		}
		
		request.setEncUserLoginSessionID(decryptedUserSessionID);
		
		return request;
	}
	
}