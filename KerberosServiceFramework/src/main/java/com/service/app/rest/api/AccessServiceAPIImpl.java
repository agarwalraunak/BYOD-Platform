/**
 * 
 */
package com.service.app.rest.api;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.app.rest.representation.AccessServiceRequest;
import com.service.app.rest.representation.AccessServiceResponse;
import com.service.model.SessionDirectory;
import com.service.model.app.AppSession;
import com.service.model.app.UserSession;
import com.service.rest.exception.common.AuthenticatorValidationException;
import com.service.rest.exception.common.UnauthenticatedAppException;
import com.service.rest.exception.common.UnauthenticatedUserException;
import com.service.util.dateutil.IDateUtil;
import com.service.util.encryption.IEncryptionUtil;
import com.service.util.hashing.IHashUtil;

/**
 * @author raunak
 *
 */
@Component
public class AccessServiceAPIImpl implements IAccessServiceAPI{
	
	private static Logger log = Logger.getLogger(AccessServiceAPIImpl.class);

	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IHashUtil iHashUtil;
	private @Autowired SessionDirectory sessionDirectory;
	
	
	@Override
	public Map<String, String> processAccessServiceRequest(AccessServiceRequest request) throws UnauthenticatedAppException, UnauthenticatedUserException, AuthenticatorValidationException {
		
		log.debug("Entering processAccessServiceRequest");
		
		if (request == null){
			log.debug("Invalid input parameter provided to processAccessServiceRequest");
			return null;
		}
		
		String appID = request.getAppID();
		String  encAppSessionID = request.getEncAppSessionID();
		Map<String, String> encData = request.getData();
		String encRequestAuthenticator = request.getEncAuthenticator();
		String encUserSessionID = request.getEncUserSessionID();
		
		AppSession appServiceSession = sessionDirectory.findAppSessionByAppID(appID);
		
		if (appServiceSession == null){
			log.error("Invalid App Username found");
			throw new UnauthenticatedAppException();
		}
		
		SecretKey appServiceSessionKey = iEncryptionUtil.generateSecretKey(appServiceSession.getKerberosServiceSessionID());
		
		String decAppSessionID = iEncryptionUtil.decrypt(appServiceSessionKey, encAppSessionID)[0];
		
		if (!iEncryptionUtil.validateDecryptedAttributes(decAppSessionID) || !decAppSessionID.equals(appServiceSession.getSessionID())){
			log.error("Invalid App Session ID found");
			throw new UnauthenticatedAppException();
		}
		
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(decAppSessionID);
		
		String[] decryptedData = iEncryptionUtil.decrypt(appSessionKey, encRequestAuthenticator, encUserSessionID);
		
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			log.error("Unable to decrypt the request attributes. Request Invalid!");
			throw new UnauthenticatedAppException();
		}
		
		String requestAuthenticatorStr = decryptedData[0];
		String userSessionID = decryptedData[1];
		
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		UserSession userSession = appServiceSession.findUserSessionBySessionID(userSessionID);
		
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
		
		Map<String, String> decData = iEncryptionUtil.decrypt(userSessionKey, encData);
		
		log.debug("Returning from processAccessServiceRequest");
		
		return decData;
	}
	
	@Override
	public AccessServiceResponse generateAccessServiceResponse(AccessServiceRequest request, Map<String, String> responseData) {
		
		log.debug("Entering generateAccessServiceResponse");
		
		if (request == null){
			log.error("Invalid Input parameter provided to generateAccessServiceResponse");
			return null;
		}
		
		String encRequestAuthenticator = request.getEncAuthenticator();
		String  encAppSessionID = request.getEncAppSessionID();
		String encUserSessionID = request.getEncUserSessionID();
		String appID = request.getAppID();
		
		AppSession appServiceSession =sessionDirectory.findAppSessionByAppID(appID);
		
		SecretKey appServiceSessionKey = iEncryptionUtil.generateSecretKey(appServiceSession.getKerberosServiceSessionID());
		
		String decAppSessionID = iEncryptionUtil.decrypt(appServiceSessionKey, encAppSessionID)[0];
		
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(decAppSessionID);
		
		String[] decryptedData = iEncryptionUtil.decrypt(appSessionKey, encRequestAuthenticator, encUserSessionID);
		String requestAuthenticatorStr = decryptedData[0];
		String userSessionID = decryptedData[1];
		
		UserSession userSession = appServiceSession.findUserSessionBySessionID(userSessionID);
		
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		
		Date responseAuthenticator = iDateUtil.createResponseAuthenticator(requestAuthenticator);
		
		//Adding authenticators to session
		appServiceSession.addAuthenticator(requestAuthenticator);
		userSession.addAuthenticator(requestAuthenticator);
		userSession.addAuthenticator(responseAuthenticator);
		appServiceSession.addAuthenticator(responseAuthenticator);
		
		SecretKey userSessionKey = iEncryptionUtil.generateSecretKey(userSessionID);
		
		String encResponseAuthenticator = iEncryptionUtil.encrypt(userSessionKey, iDateUtil.generateStringFromDate(responseAuthenticator))[0];
		Map<String, String> encResponseData = iEncryptionUtil.encrypt(userSessionKey, responseData);
		
		AccessServiceResponse response = new AccessServiceResponse();
		response.setData(encResponseData);
		response.setEncAuthenticator(encResponseAuthenticator);
		
		
		return response;
	}
}