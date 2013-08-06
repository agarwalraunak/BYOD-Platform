package com.kerberos.device.rest.api;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kerberos.device.applicationdetailservice.ApplicationDetailService;
import com.kerberos.device.model.AppServiceSession;
import com.kerberos.device.model.ServiceTicket;
import com.kerberos.device.model.UserServiceSession;
import com.kerberos.device.rest.exceptions.UnauthorizedResponseException;
import com.kerberos.device.rest.representation.kerberos.service.AccessServiceRequest;
import com.kerberos.device.rest.representation.kerberos.service.AccessServiceResponse;
import com.kerberos.device.rest.representation.kerberos.service.AppAuthenticationRequest;
import com.kerberos.device.rest.representation.kerberos.service.UserLoginRequest;
import com.kerberos.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.kerberos.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.kerberos.device.util.connectionmanager.IConnectionManager;
import com.kerberos.device.util.dateutil.IDateUtil;
import com.kerberos.device.util.encryption.IEncryptionUtil;

public class ClientAccessServiceAPIImpl implements IClientAccessServiceAPI{

	private static Logger log = Logger.getLogger(ClientAccessServiceAPIImpl.class);
	
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired ApplicationDetailService applicationDetailService;
	private @Autowired IConnectionManager iConnectionManager;
	
	@Override
	public AppAuthenticationRequest createAppAuthenticationRequest(SecretKey serviceSessionKey, String serviceTicketPacket, String requestAuthenticator) throws InvalidAttributeValueException{
		
		log.debug("Entering createAppAuthenticationRequest method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(serviceTicketPacket, requestAuthenticator) || serviceSessionKey == null){
			log.error("Invalid input parameter provided to createAppAuthenticationRequest");
			throw new InvalidAttributeValueException("Invalid input parameter provided to createAppAuthenticationRequest");
		}
		
		String encRequestAuthenticator = iEncryptionUtil.encrypt(serviceSessionKey, requestAuthenticator)[0];
		
		AppAuthenticationRequest appAuthenticationRequest = new AppAuthenticationRequest();
		appAuthenticationRequest.setEncAuthenticator(encRequestAuthenticator);
		appAuthenticationRequest.setServiceTicketPacket(serviceTicketPacket);
		
		log.debug("Returning from createAppAuthenticationRequest");
		
		return appAuthenticationRequest;
	}
	
	@Override
	public AppServiceSession processAuthenticateAppResponse(String encAppSessionID, String encResponseAuthenticator, Date requestAuthenticator, ServiceTicket serviceTicket, SecretKey serviceSessionKey) throws InvalidAttributeValueException{
		
		log.debug("Entering processAuthenticateAppResponse method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(encAppSessionID, encResponseAuthenticator) || serviceSessionKey == null || serviceTicket == null){
			log.error("Invalid input parameter provided to processAuthenticateAppResponse");
			throw new InvalidAttributeValueException("Invalid input parameter provided to processAuthenticateAppResponse");
		}
		
		String[] decryptedData = iEncryptionUtil.decrypt(serviceSessionKey, encAppSessionID, encResponseAuthenticator);
		
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			return null;
		}
		
		String appSessionID = decryptedData[0];
		String responseAuthenticator = decryptedData[1];
		
		//Create the App Service Session
		AppServiceSession appServiceSession = serviceTicket.createAppServiceSession(appSessionID);
		
		appServiceSession.addAuthenticator(requestAuthenticator);
		appServiceSession.addAuthenticator(iDateUtil.generateDateFromString(responseAuthenticator));
		
		log.debug("Returning from processAuthenticateAppResponse");
		
		return appServiceSession;
		
	}
	
	@Override
	public UserLoginRequest createUserLoginRequest(String username, String password, String requestAuthenticationStr, String appSessionID, String appUsername, SecretKey appSessionIDKey, SecretKey serviceSessionKey) throws InvalidAttributeValueException {
		
		log.debug("Entering createUserLoginRequest");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(username, password, requestAuthenticationStr, appSessionID, appUsername) || appSessionIDKey == null || serviceSessionKey == null){
			log.error("Invalid input parameter provided to createUserLoginRequest");
			throw new InvalidAttributeValueException("Invalid input parameter provided to createUserLoginRequest");
		}
		
		String[] encryptedData = iEncryptionUtil.encrypt(appSessionIDKey, username, password, requestAuthenticationStr);
		String encUsername = encryptedData[0];
		String encPassword = encryptedData[1];
		String encRequestAuthenticatorStr = encryptedData[2];
		
		String encAppSessionID= iEncryptionUtil.encrypt(serviceSessionKey, appSessionID)[0];
		
		UserLoginRequest request = new UserLoginRequest();
		request.setAppID(appUsername);
		request.setEncAppSessionID(encAppSessionID);
		request.setEncAuthenticator(encRequestAuthenticatorStr);
		request.setEncPassword(encPassword);
		request.setEncUsername(encUsername);
		
		return request;
	}
	
	@Override
	public UserServiceSession processUserLoginResponse(String encUsername, String encUserSessionID, String encResponseAuthenticator, AppServiceSession appServiceSession, Date requestAuthenticator, SecretKey appSessionIDKey) throws InvalidAttributeValueException {
		
		log.debug("Entering processUserLoginResponse method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(encUsername, encUserSessionID, encResponseAuthenticator)){
			log.error("Invalid input parameter provided to processUserLoginrResponse method");
			throw new InvalidAttributeValueException("Invalid input parameter provided to processUserLoginrResponse method");
		}
		
		String[] decryptedData = iEncryptionUtil.decrypt(appSessionIDKey, encUsername, encUserSessionID, encResponseAuthenticator);
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			log.debug("Validation of UserLoginResponse failed. User session would not be created");
			return null;
		}
		
		String username = decryptedData[0];
		String userSessionID = decryptedData[1];
		String responseAuthenticator = decryptedData[2];
		
		UserServiceSession userServiceSession = appServiceSession.createUserServiceSession(username, userSessionID);
		
		userServiceSession.addAuthenticator(requestAuthenticator);
		userServiceSession.addAuthenticator(iDateUtil.generateDateFromString(responseAuthenticator));
		
		log.debug("Returning from processUserLoginRequest");
		
		return userServiceSession;
	}

	@Override
	public AccessServiceResponse generateAccessRequest(String url, RequestMethod requestMethod, ContentType contentType, String appID, String appSessionID, String requestAuthenticator, String userServiceSessionID, 
			Map<String, String> data, SecretKey kerberosAppServiceSessionKey, SecretKey appSessionKey, SecretKey userSessionKey) throws InvalidAttributeValueException, IOException{
		
		log.debug("Entering generateAccessRequest");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(url, appID, appSessionID, requestAuthenticator, userServiceSessionID) || kerberosAppServiceSessionKey == null || appSessionKey == null || userSessionKey == null){
			log.error("Invalid input parameter provided to generateAccessRequest");
			throw new InvalidAttributeValueException("Invalid input parameter provided to generateAccessRequest");
		}
		
		String encAppSessionID = iEncryptionUtil.encrypt(kerberosAppServiceSessionKey, appSessionID)[0];
		String[] encryptedData = iEncryptionUtil.encrypt(appSessionKey, requestAuthenticator, userServiceSessionID);
		String encRequestAuthenticator = encryptedData[0];
		String encUserServiceSessionID = encryptedData[1];
		
		Map<String, String> encData = null;
		if (data != null && data.keySet().size() > 0){
			encData = new HashMap<String, String>();
			Iterator<String> iterator = data.keySet().iterator();
			String key = null;
			while(iterator.hasNext()){
				key = iterator.next();
				encData.put(key, iEncryptionUtil.encrypt(userSessionKey, data.get(key))[0]);
			}
		}
		
		AccessServiceRequest accessServiceRequest = new AccessServiceRequest();
		accessServiceRequest.setAppID(appID);
		accessServiceRequest.setData(encData);
		accessServiceRequest.setEncAppSessionID(encAppSessionID);
		accessServiceRequest.setEncAuthenticator(encRequestAuthenticator);
		accessServiceRequest.setEncUserSessionID(encUserServiceSessionID);
		
		AccessServiceResponse response = (AccessServiceResponse) iConnectionManager.generateRequest(url, requestMethod, contentType, AccessServiceResponse.class, iConnectionManager.generateJSONStringForObject(accessServiceRequest));
		
		log.debug("Returning from generateAccessRequest method");
		
		return response;
	}
	
	@Override
	public Map<String, String> processAccessResponse(String requestAuthenticatorStr, String encResponseAuthenticator, Map<String, String> encData, AppServiceSession appServiceSession, UserServiceSession userServiceSession, SecretKey userSessionKey) throws InvalidAttributeValueException, UnauthorizedResponseException{
		
		log.debug("Entering processAccessResponse method");
		
		if (encData == null || encData.size() == 0){
			return null;
		}
		
		if (!iEncryptionUtil.validateDecryptedAttributes(encResponseAuthenticator) || userSessionKey == null){
			log.error("Invalid input parameter provided to processAccessResponse method");
			throw new InvalidAttributeValueException("Invalid input parameter provided to processAccessResponse method");
		}
		
		String responseAuthenticatorStr = iEncryptionUtil.decrypt(userSessionKey, encResponseAuthenticator)[0];
		if (!iEncryptionUtil.validateDecryptedAttributes(responseAuthenticatorStr)){
			log.error("Response Data failed to decrypt. Unauthorized response found");
			throw new UnauthorizedResponseException("Response Data failed to decrypt. Unauthorized response found");
		}
		
		Date responseAuthenticator = iDateUtil.generateDateFromString(responseAuthenticatorStr);
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		if (!iDateUtil.validateAuthenticator(responseAuthenticator, requestAuthenticator)){
			log.error("Authenticator failed to validate. Unauthorized response found");
			throw new UnauthorizedResponseException("Authenticator failed to validate. Unauthorized response found");
		}
		
		appServiceSession.addAuthenticator(requestAuthenticator);
		appServiceSession.addAuthenticator(responseAuthenticator);
		
		userServiceSession.addAuthenticator(requestAuthenticator);
		userServiceSession.addAuthenticator(responseAuthenticator);
		
		
		
		Map<String, String> data  = null;
		if (encData != null && encData.size() > 0){
			data = new HashMap<>();
			Iterator<String> iterator = encData.keySet().iterator();
			String key = null;
			while(iterator.hasNext()){
				key = iterator.next();
				data.put(key, iEncryptionUtil.decrypt(userSessionKey, encData.get(key))[0]);
			}
		}
		return data;
	}
}





