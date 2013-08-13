package com.login.service.rest.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.app.model.AppSession;
import com.login.app.model.AppSessionDirectory;
import com.login.rest.exceptions.UnauthenticatedAppException;
import com.login.rest.exceptions.UnauthenticatedRequestException;
import com.login.service.rest.representation.AppAccessServiceRequest;
import com.login.service.rest.representation.AppAccessServiceResponse;
import com.login.util.dateutil.IDateUtil;
import com.login.util.encryption.IEncryptionUtil;

@Component
public class AppAccessServiceAPIImpl implements IAppAccessServiceAPI{
	
	private static Logger log = Logger.getLogger(AppAccessServiceAPIImpl.class);
	
	private @Autowired AppSessionDirectory appSessionDirectory;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	
	@Override
	public Map<String, String> processAppAccessServiceRequest(AppAccessServiceRequest request) throws InvalidAttributeValueException{
		
		log.debug("Entering processAppAccessServiceRequest");
		
		if (request == null){
			log.error("Invalid input parameter provided to processAppAccessServiceRequest");
			throw new InvalidAttributeValueException("Invalid input parameter provided to processAppAccessServiceRequest");
		}
		
		String appID = request.getAppID();
		String encAppSessionID = request.getEncAppSessionID();
		String encAuthenticator = request.getEncAuthenticator();
		Map<String, String> encRequestData = request.getData();
		
		AppSession appSession = appSessionDirectory.findAppSessionByAppID(appID);
		
		//If the Session app does not exist throw an Unauthorized Exception
		if (appSession == null){
			log.error("Request from Unauthorized app found");
			throw new UnauthenticatedAppException("Request from Unauthorized app found", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		String serviceSessionID = appSession.getAppServiceSessionID();
		SecretKey serviceSessionKey = iEncryptionUtil.generateSecretKey(serviceSessionID);
		
		//Using the Service Session Key to get the App Session ID
		String decAppSessionID = iEncryptionUtil.decrypt(serviceSessionKey, encAppSessionID)[0];
		if (!iEncryptionUtil.validateDecryptedAttributes(decAppSessionID)){
			log.error("Request from Unauthorized app found");
			throw new UnauthenticatedAppException("Request from Unauthorized app found", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		//Validate if the received AppSessionID is right
		if (!decAppSessionID.equals(appSession.getAppSessionID())){
			log.error("Request from Unauthorized app found");
			throw new UnauthenticatedAppException("Request from Unauthorized app found", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(decAppSessionID);
		//Decrypt the Authenticator with the AppSessionKey
		String requestAuthenticatorStr = iEncryptionUtil.decrypt(appSessionKey, encAuthenticator)[0];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		//Validate the authenticator
		if (!appSession.validateAuthenticator(requestAuthenticator)){
			log.error("Invalid Authenticator found");
			throw new UnauthenticatedRequestException("Unauthenticated request found", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		//Decrypt the request data
		Map<String, String> decData = decryptRequestData(encRequestData, appSessionKey);
		
		log.debug("Returning from processAppAccessServiceRequest");
		
		return decData;
	}
	
	@Override
	public AppAccessServiceResponse generateAppAccessServiceResponse(AppAccessServiceRequest request, Map<String, String> responseData) throws InvalidAttributeValueException{
		
		if (request == null){
			log.error("Invalid Input parameter provided to generateAppAccessServiceResponse");
			throw new InvalidAttributeValueException("Invalid Input parameter provided to generateAppAccessServiceResponse");
		}
		
		String appID = request.getAppID();
		String encRequestAuthenticator = request.getEncAuthenticator();
		
		//Get the app session
		AppSession appSession = appSessionDirectory.findAppSessionByAppID(appID);
		String appSessionID = appSession.getAppSessionID();
		//Generate key using the app session
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSessionID);
		
		//Get the request authenticator
		String requestAuthenticatorStr = iEncryptionUtil.decrypt(appSessionKey, encRequestAuthenticator)[0];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		
		//Encrypting the response data
		Map<String, String> encResponseData = encryptResponseData(responseData, appSessionKey);
		
		//Creating the Response Authenticator
		Date responseAuthenticator = iDateUtil.createResponseAuthenticator(requestAuthenticator);
		String responseAuthenticatorStr = iDateUtil.generateStringFromDate(responseAuthenticator);
		String encResponseAuthenticator = iEncryptionUtil.encrypt(appSessionKey, responseAuthenticatorStr)[0];
		
		//Adding the Authenticator to App Session
		appSession.addAuthenticator(requestAuthenticator);
		appSession.addAuthenticator(responseAuthenticator);
				
		//Generating the response
		AppAccessServiceResponse response = new AppAccessServiceResponse();
		response.setEncResponseAuthenticator(encResponseAuthenticator);
		response.setEncResponseData(encResponseData);
		
		log.debug("Returning from generateAppAccessServiceResponse");
		
		return response;
	}
	
	public Map<String, String> encryptResponseData(Map<String, String> responseData, SecretKey appSessionKey) throws InvalidAttributeValueException{
		
		log.debug("Entering encryptResponseData");
		
		if (responseData == null || appSessionKey == null){
			log.error("Invalid input parameter provided to encryptResponseData");
			throw new InvalidAttributeValueException("Invalid input parameter provided to encryptResponseData");
		}
		
		Map<String, String> encResponseData = null;
		if (responseData.size() > 0){
			Iterator<String> iterator = responseData.keySet().iterator();
			encResponseData = new HashMap<>();
			String key = null;
			while(iterator.hasNext()){
				key = iterator.next();
				encResponseData.put(key, iEncryptionUtil.encrypt(appSessionKey, responseData.get(key))[0]);
			}
		}
		
		log.debug("Returning from encryptResponseData");
		
		return encResponseData;
	}

	/**
	 * @param encRequestData
	 * @param appSessionKey
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	public Map<String, String> decryptRequestData(Map<String, String> encRequestData, SecretKey appSessionKey) throws InvalidAttributeValueException{
		
		log.debug("Entering decryptRequestData");
		
		if (encRequestData == null || appSessionKey == null){
			log.error("Invalid Input parameter to decryptRequestData");
			throw new InvalidAttributeValueException("Invalid Input parameter to decryptRequestData");
		}
		
		Iterator<String> iterator = encRequestData.keySet().iterator();
		String key = null;
		Map<String, String> decData = new HashMap<>();
		while(iterator.hasNext()){
			key = iterator.next();
			decData.put(key, iEncryptionUtil.decrypt(appSessionKey, encRequestData.get(key))[0]);
		}
		
		log.debug("Returning from decryptRequestData");
		
		return decData;
	}
}
