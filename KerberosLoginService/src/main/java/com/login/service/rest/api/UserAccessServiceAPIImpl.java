/**
 * 
 */
package com.login.service.rest.api;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.exception.common.AuthenticatorValidationException;
import com.login.exception.common.UnauthenticatedAppException;
import com.login.exception.common.UnauthenticatedUserException;
import com.login.model.SessionDirectory;
import com.login.model.app.AppSession;
import com.login.model.app.UserSession;
import com.login.service.rest.representation.UserAccessServiceRequest;
import com.login.service.rest.representation.UserAccessServiceResponse;
import com.login.util.dateutil.IDateUtil;
import com.login.util.encryption.IEncryptionUtil;
import com.login.util.hashing.IHashUtil;

/**
 * @author raunak
 *
 */
@Component
public class UserAccessServiceAPIImpl implements IUserAccessServiceAPI{
	
	private static Logger log = Logger.getLogger(UserAccessServiceAPIImpl.class);

	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IHashUtil iHashUtil;
	private @Autowired SessionDirectory sessionDirectory;
	
	
	@Override
	public Map<String, String> processAccessServiceRequest(UserAccessServiceRequest request) throws UnauthenticatedAppException, AuthenticatorValidationException, UnauthenticatedUserException{
		
		log.debug("Entering processAccessServiceRequest");
		
		if (request == null){
			log.debug("Invalid input parameter provided to processAccessServiceRequest");
			throw new IllegalArgumentException("Invalid input parameter provided to processAccessServiceRequest");
		}
		
		String appID = request.getAppID();
		
		AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(appID);
		
		if (appSession == null){
			log.error("Invalid App Username found");
			throw new UnauthenticatedAppException();
		}
		
		SecretKey appServiceSessionKey = iEncryptionUtil.generateSecretKey(appSession.getKerberosServiceSessionID());
		String  encAppSessionID = request.getEncAppSessionID();
		String decAppSessionID = iEncryptionUtil.decrypt(appServiceSessionKey, encAppSessionID)[0];
		
		//Validating the Decrypted App Session ID 
		//Checking if the App Session ID in the request is the same with the server
		if (!iEncryptionUtil.validateDecryptedAttributes(decAppSessionID) || !decAppSessionID.equals(appSession.getSessionID())){
			log.error("Invalid App Session ID found");
			throw new UnauthenticatedAppException();
		}
		
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(decAppSessionID);
		
		String encUserSessionID = request.getEncUserSessionID();
		String encRequestAuthenticator = request.getEncAuthenticator();
		
		//Decrypting the Request Parameters
		String[] decryptedData = iEncryptionUtil.decrypt(appSessionKey, encRequestAuthenticator, encUserSessionID);
		//Validate the decrypted parameters
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			log.error("Unable to decrypt the request attributes. Request Invalid!");
			throw new UnauthenticatedAppException();
		}
		String requestAuthenticatorStr = decryptedData[0];
		String userSessionID = decryptedData[1];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		
		//validate the request authenticator
		if (!appSession.validateAuthenticator(requestAuthenticator)){
			log.error("Validation of the User Access Service Request authenticator failed");
			throw new AuthenticatorValidationException();
		}
		
		UserSession userSession = appSession.findActiveUserSessionBySessionID(userSessionID);
		
		//Check if the User Session exists for the user session id
		if (userSession == null){
			log.error("User Session ID does not exist. Request Invalid!");
			throw new UnauthenticatedUserException();
		}
		
		//Validate the authenticator
		if (!userSession.validateAuthenticator(requestAuthenticator)){
			log.error("Invalid Authenticator found. Request Invalid!");
			throw new AuthenticatorValidationException();
		}
		
		SecretKey userSessionKey = iEncryptionUtil.generateSecretKey(userSessionID);
		Map<String, String> encData = request.getData();
		Map<String, String> decData = iEncryptionUtil.decrypt(userSessionKey, encData);

		log.debug("Returning from processAccessServiceRequest");
		
		return decData;
	}
	
	@Override
	public UserAccessServiceResponse generateAccessServiceResponse(UserAccessServiceRequest request, Map<String, String> responseData) throws UnauthenticatedAppException, UnauthenticatedUserException {
		
		if (request == null){
			log.error("Invalid Input parameter provided to generateAccessServiceResponse");
			throw new IllegalArgumentException("Invalid Input parameter provided to generateAccessServiceResponse");
		}
		
		String appID = request.getAppID();
		
		AppSession appSession =sessionDirectory.findActiveAppSessionByAppID(appID);
		
		if (appSession == null){
			throw new UnauthenticatedAppException();
		}
		
		SecretKey appServiceSessionKey = iEncryptionUtil.generateSecretKey(appSession.getKerberosServiceSessionID());
		String  encAppSessionID = request.getEncAppSessionID();
		String decAppSessionID = iEncryptionUtil.decrypt(appServiceSessionKey, encAppSessionID)[0];
		if (decAppSessionID == null || decAppSessionID.isEmpty()){
			throw new UnauthenticatedAppException();
		}
		
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(decAppSessionID);
		
		String encRequestAuthenticator = request.getEncAuthenticator();
		String encUserSessionID = request.getEncUserSessionID();
		String[] decryptedData = iEncryptionUtil.decrypt(appSessionKey, encRequestAuthenticator, encUserSessionID);
		
		if (iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			throw new UnauthenticatedAppException();
		}

		String requestAuthenticatorStr = decryptedData[0];
		String userSessionID = decryptedData[1];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		
		UserSession userSession = appSession.findActiveUserSessionBySessionID(userSessionID);
		if (userSession == null){
			throw new UnauthenticatedUserException();
		}
		
		Date responseAuthenticator = iDateUtil.createResponseAuthenticator(requestAuthenticator);
		
		//Adding authenticators to session
		appSession.addAuthenticator(requestAuthenticator);
		userSession.addAuthenticator(requestAuthenticator);
		userSession.addAuthenticator(responseAuthenticator);
		appSession.addAuthenticator(responseAuthenticator);
		
		SecretKey userSessionKey = iEncryptionUtil.generateSecretKey(userSessionID);
		
		String encResponseAuthenticator = iEncryptionUtil.encrypt(userSessionKey, iDateUtil.generateStringFromDate(responseAuthenticator))[0];
		Map<String, String> encResponseData = iEncryptionUtil.encrypt(userSessionKey, responseData);
		
		UserAccessServiceResponse response = new UserAccessServiceResponse();
		response.setData(encResponseData);
		response.setEncResponseAuthenticator(encResponseAuthenticator);
		
		
		return response;
	}
}