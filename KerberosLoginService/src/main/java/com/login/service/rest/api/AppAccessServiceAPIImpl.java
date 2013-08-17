package com.login.service.rest.api;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.exception.common.AuthenticatorValidationException;
import com.login.exception.common.UnauthenticatedAppException;
import com.login.model.SessionDirectory;
import com.login.model.app.AppSession;
import com.login.service.rest.representation.AppAccessServiceRequest;
import com.login.service.rest.representation.AppAccessServiceResponse;
import com.login.util.dateutil.IDateUtil;
import com.login.util.encryption.IEncryptionUtil;

@Component
public class AppAccessServiceAPIImpl implements IAppAccessServiceAPI{
	
	private static Logger log = Logger.getLogger(AppAccessServiceAPIImpl.class);
	
	private @Autowired SessionDirectory sessionDirectory;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	
	public Map<String, String> processAppAccessServiceRequest(AppAccessServiceRequest request) throws AuthenticatorValidationException, UnauthenticatedAppException{
		
		log.debug("Entering processAppAccessServiceRequest");
		
		if (request == null){
			log.error("Invalid input parameter provided to processAppAccessServiceRequest");
			throw new IllegalArgumentException("Invalid input parameter provided to processAppAccessServiceRequest");
		}
		
		String appID = request.getAppID();
		
		AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(appID);
		//If the Session app does not exist throw an Unauthorized Exception
		if (appSession == null){
			log.error("Request from Unauthorized app found");
			throw new UnauthenticatedAppException();
		}
		
		String serviceSessionID = appSession.getKerberosServiceSessionID();
		SecretKey serviceSessionKey = iEncryptionUtil.generateSecretKey(serviceSessionID);
		
		//Using the Service Session Key to get the App Session ID
		String encAppSessionID = request.getEncAppSessionID();
		String decAppSessionID = iEncryptionUtil.decrypt(serviceSessionKey, encAppSessionID)[0];
		if (!iEncryptionUtil.validateDecryptedAttributes(decAppSessionID)){
			log.error("Request from Unauthorized app found");
			throw new UnauthenticatedAppException();
		}
		
		//Validate if the received AppSessionID is right
		if (!decAppSessionID.equals(appSession.getSessionID())){
			log.error("Request from Unauthorized app found");
			throw new UnauthenticatedAppException();
		}
		
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(decAppSessionID);
		//Decrypt the Authenticator with the AppSessionKey
		String encAuthenticator = request.getEncAuthenticator();
		String requestAuthenticatorStr = iEncryptionUtil.decrypt(appSessionKey, encAuthenticator)[0];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		//Validate the authenticator
		if (!appSession.validateAuthenticator(requestAuthenticator)){
			log.error("Invalid Authenticator found");
			throw new AuthenticatorValidationException();
		}
		
		//Decrypt the request data
		Map<String, String> encRequestData = request.getData();
		Map<String, String> decData = iEncryptionUtil.decrypt(appSessionKey, encRequestData);
		
		log.debug("Returning from processAppAccessServiceRequest");
		
		return decData;
	}
	
	@Override
	public AppAccessServiceResponse generateAppAccessServiceResponse(AppAccessServiceRequest request, Map<String, String> responseData) {
		
		if (request == null){
			log.error("Invalid Input parameter provided to generateAppAccessServiceResponse");
			throw new IllegalArgumentException("Invalid Input parameter provided to generateAppAccessServiceResponse");
		}
		
		String appID = request.getAppID();
		String encRequestAuthenticator = request.getEncAuthenticator();
		
		//Get the app session
		AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(appID);
		String appSessionID = appSession.getSessionID();
		//Generate key using the app session
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSessionID);
		
		//Get the request authenticator
		String requestAuthenticatorStr = iEncryptionUtil.decrypt(appSessionKey, encRequestAuthenticator)[0];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		
		//Encrypting the response data
		Map<String, String> encResponseData = iEncryptionUtil.encrypt(appSessionKey, responseData);
		
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
}