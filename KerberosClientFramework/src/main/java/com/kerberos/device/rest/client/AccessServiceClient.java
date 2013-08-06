/**
 * 
 */
package com.kerberos.device.rest.client;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kerberos.device.applicationdetailservice.ApplicationDetailService;
import com.kerberos.device.model.AppServiceSession;
import com.kerberos.device.model.KerberosSessionManager;
import com.kerberos.device.model.ServiceTicket;
import com.kerberos.device.model.UserServiceSession;
import com.kerberos.device.rest.api.IClientAccessServiceAPI;
import com.kerberos.device.rest.exceptions.UnauthorizedResponseException;
import com.kerberos.device.rest.representation.kerberos.service.AccessServiceResponse;
import com.kerberos.device.rest.representation.kerberos.service.AppAuthenticationRequest;
import com.kerberos.device.rest.representation.kerberos.service.AppAuthenticationResponse;
import com.kerberos.device.rest.representation.kerberos.service.UserLoginRequest;
import com.kerberos.device.rest.representation.kerberos.service.UserLoginResponse;
import com.kerberos.device.util.connectionmanager.IConnectionManager;
import com.kerberos.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.kerberos.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.kerberos.device.util.dateutil.IDateUtil;
import com.kerberos.device.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */
public class AccessServiceClient {
	
	private static Logger log = Logger.getLogger(AccessServiceClient.class);
	
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IConnectionManager iConnectionManager;
	private @Autowired IClientAccessServiceAPI iClientAccessServiceAPI;
	private @Autowired ApplicationDetailService applicationDetailService;
	private @Autowired KerberosSessionManager kerberosSessionManager;
	
	/**
	 * @param url
	 * @param serviceTicket
	 * @return
	 * @throws InvalidAttributeValueException
	 * @throws IOException
	 */
	public AppServiceSession authenticateAppServiceTicket(String url, ServiceTicket serviceTicket) throws InvalidAttributeValueException, IOException{
		
		log.debug("Entering authenticateAppServiceTicket");
		
		if (url == null || url.isEmpty() || serviceTicket == null){
			log.error("Invalid input parameter provided to authenticateAppServiceTicket");
			throw new InvalidAttributeValueException("Invalid input parameter provided to authenticateAppServiceTicket");
		}
		
		AppServiceSession appServiceSession = serviceTicket.getAppServiceSession();
		if (appServiceSession != null){
			return appServiceSession;
		}
		
		String requestAuthenticator = iDateUtil.createAuthenticator();
		SecretKey serviceSessionKey = iEncryptionUtil.generateSecretKey(serviceTicket.getServiceSessionID());
		AppAuthenticationRequest request = iClientAccessServiceAPI.createAppAuthenticationRequest(serviceSessionKey, serviceTicket.getEncServiceTicket(), requestAuthenticator);
		
		AppAuthenticationResponse response = (AppAuthenticationResponse)iConnectionManager.generateRequest(url, RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, AppAuthenticationResponse.class, iConnectionManager.generateJSONStringForObject(request));
		
		appServiceSession = iClientAccessServiceAPI.processAuthenticateAppResponse(response.getEncAppSessionID(), response.getEncResponseAuthenticator(), iDateUtil.generateDateFromString(requestAuthenticator),
				serviceTicket, serviceSessionKey);
		
		log.debug("Returning from authenticateAppServiceTicket method");
		
		return appServiceSession;
	}
	
	/**
	 * @param url
	 * @param serviceTicket
	 * @param username
	 * @param password
	 * @return
	 * @throws InvalidAttributeValueException
	 * @throws IOException
	 */
	public UserServiceSession authenticateUser(String url, ServiceTicket serviceTicket, String username, String password) throws InvalidAttributeValueException, IOException{
	
		log.debug("Entering authenticateUser method");
		
		if (url == null || url.isEmpty() || serviceTicket == null || username == null || username.isEmpty() || password == null || password.isEmpty()){
			log.error("Invalid input parameter provided to authenticateUser");
			throw new InvalidAttributeValueException("Invalid input parameter provided to authenticateUser");
		}
		
		AppServiceSession appServiceSession = serviceTicket.getAppServiceSession();
		
		UserServiceSession userServiceSession = appServiceSession.findUserServiceSessionByUsername(username);
		if (userServiceSession != null){
			return userServiceSession;
		}
		
		String appSessionID = appServiceSession.getSessionID();
		String serviceSessionID = serviceTicket.getServiceSessionID();
		
		SecretKey appSessionIDKey = iEncryptionUtil.generateSecretKey(appSessionID);
		SecretKey serviceSessionKey = iEncryptionUtil.generateSecretKey(serviceSessionID);
		
		String requestAuthenticatorStr = iDateUtil.createAuthenticator();
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		
		UserLoginRequest request = iClientAccessServiceAPI.createUserLoginRequest(username, password, requestAuthenticatorStr, appSessionID, applicationDetailService.getAppLoginName() , appSessionIDKey, serviceSessionKey);
		UserLoginResponse response = (UserLoginResponse)iConnectionManager.generateRequest(url, RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, UserLoginResponse.class, iConnectionManager.generateJSONStringForObject(request));
		
		userServiceSession = iClientAccessServiceAPI.processUserLoginResponse(response.getEncUsername(), response.getEncUserSessionID(), response.getEncResponseAuthenticator(), appServiceSession, requestAuthenticator, appSessionIDKey);
		
		if (userServiceSession == null){
			log.debug("Failed to create a service session for the user");
		}
		
		return userServiceSession;
	}
	
	
	/**
	 * @param url
	 * @param requestMethod
	 * @param contentType
	 * @param serviceTicket
	 * @param username
	 * @param data
	 * @return data sent by the Service
	 * @throws InvalidAttributeValueException
	 * @throws IOException
	 * @throws UnauthorizedResponseException 
	 */
	public Map<String, String> accessService(String url, RequestMethod requestMethod, ContentType contentType, ServiceTicket serviceTicket, String username, Map<String, String> data) throws InvalidAttributeValueException, IOException, UnauthorizedResponseException{
		
		AppServiceSession appServiceSession = serviceTicket.getAppServiceSession();
		UserServiceSession userServiceSession = appServiceSession.findUserServiceSessionByUsername(username);
		String appID = applicationDetailService.getAppLoginName();
		String kerberosAppServiceSessionID = serviceTicket.getServiceSessionID();
		String appSessionID = appServiceSession.getSessionID();
		String  userServiceSessionID = userServiceSession.getUserSessionID();
		String requestAuthenticator = iDateUtil.generateStringFromDate(userServiceSession.createAuthenticator());
		
		if (!iEncryptionUtil.validateDecryptedAttributes(appID, appSessionID, userServiceSessionID)){
			log.error("Invalid Access Request from unauthenticated App and User");
			throw new IOException("Invalid Access Request from unauthenticated App and User");
		}
		
		SecretKey kerberosAppServiceSessionKey = iEncryptionUtil.generateSecretKey(kerberosAppServiceSessionID);
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSessionID);
		SecretKey userSessionKey = iEncryptionUtil.generateSecretKey(userServiceSessionID);
		
		AccessServiceResponse response = iClientAccessServiceAPI.generateAccessRequest(url, requestMethod, contentType, appID, appSessionID, requestAuthenticator, userServiceSessionID, data, kerberosAppServiceSessionKey, appSessionKey, userSessionKey);
		
		if (response == null){
			log.error("Service did not return any response");
			throw new IOException("Service did not return any response");
		}
		
		return iClientAccessServiceAPI.processAccessResponse(requestAuthenticator, response.getEncAuthenticator(), response.getData(), appServiceSession, userServiceSession, userSessionKey);
		
	}

}
