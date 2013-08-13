/**
 * 
 */
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
import com.login.app.model.UserSession;
import com.login.rest.exceptions.InvalidRequestException;
import com.login.rest.exceptions.UnauthenticatedAppException;
import com.login.rest.exceptions.UnauthenticatedUserException;
import com.login.service.rest.representation.AccessServiceRequest;
import com.login.service.rest.representation.AccessServiceResponse;
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
	private @Autowired AppSessionDirectory sessionDirectoryy;
	
	
	@Override
	public Map<String, String> processAccessServiceRequest(AccessServiceRequest request) throws InvalidAttributeValueException{
		
		log.debug("Entering processAccessServiceRequest");
		
		if (request == null){
			log.debug("Invalid input parameter provided to processAccessServiceRequest");
			throw new InvalidAttributeValueException("Invalid input parameter provided to processAccessServiceRequest");
		}
		
		String appID = request.getAppID();
		String  encAppSessionID = request.getEncAppSessionID();
		Map<String, String> encData = request.getData();
		String encRequestAuthenticator = request.getEncAuthenticator();
		String encUserSessionID = request.getEncUserSessionID();
		
		AppSession appServiceSession = sessionDirectoryy.findAppSessionByAppID(appID);
		
		if (appServiceSession == null){
			log.error("Invalid App Username found");
			throw new InvalidRequestException("Invalid App Username. Bad request found", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		SecretKey appServiceSessionKey = iEncryptionUtil.generateSecretKey(appServiceSession.getAppServiceSessionID());
		
		String decAppSessionID = iEncryptionUtil.decrypt(appServiceSessionKey, encAppSessionID)[0];
		
		if (!iEncryptionUtil.validateDecryptedAttributes(decAppSessionID) || !decAppSessionID.equals(appServiceSession.getAppSessionID())){
			log.error("Invalid App Session ID found");
			throw new InvalidRequestException("Invalid App Session ID. Bad request found", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(decAppSessionID);
		
		String[] decryptedData = iEncryptionUtil.decrypt(appSessionKey, encRequestAuthenticator, encUserSessionID);
		String requestAuthenticatorStr = decryptedData[0];
		String userSessionID = decryptedData[1];
		
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			log.error("Unable to decrypt the request attributes. Request Invalid!");
			throw new InvalidRequestException("Request from unauthenticated app. Request Invalid!", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		UserSession userSession = appServiceSession.findUserSessionBySessionID(userSessionID);
		
		//Check if the User Session exists for the user session id
		if (userSession == null){
			log.error("User Session ID does not exist. Request Invalid!");
			throw new InvalidRequestException("User Session ID does not exist. Request Invalid!", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		//Validate the authenticator
		if (!userSession.validateAuthenticator(requestAuthenticator)){
			log.error("Invalid Authenticator found. Request Invalid!");
			throw new InvalidRequestException("Invalid Authenticator Found. Request Invalid!", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		SecretKey userSessionKey = iEncryptionUtil.generateSecretKey(userSessionID);
		
		Map<String, String> decData = null;
		if (encData != null && encData.keySet().size() > 0){
			decData = new HashMap<>();
			Iterator<String> iterator = encData.keySet().iterator();
			String key = null;
			while(iterator.hasNext()){
				key = iterator.next();
				decData.put(key, iEncryptionUtil.decrypt(userSessionKey, encData.get(key))[0]);
			}
		}
		
		log.debug("Returning from processAccessServiceRequest");
		
		return decData;
	}
	
	@Override
	public AccessServiceResponse generateAccessServiceResponse(AccessServiceRequest request, Map<String, String> responseData) throws InvalidAttributeValueException{
		
		String encRequestAuthenticator = request.getEncAuthenticator();
		String  encAppSessionID = request.getEncAppSessionID();
		String encUserSessionID = request.getEncUserSessionID();
		
		String appID = request.getAppID();
		
		AppSession appServiceSession =sessionDirectoryy.findAppSessionByAppID(appID);
		
		if (appServiceSession == null){
			throw new UnauthenticatedAppException("Unauthenticated App found", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		SecretKey appServiceSessionKey = iEncryptionUtil.generateSecretKey(appServiceSession.getAppServiceSessionID());
		
		String decAppSessionID = iEncryptionUtil.decrypt(appServiceSessionKey, encAppSessionID)[0];
		if (decAppSessionID == null || decAppSessionID.isEmpty()){
			throw new UnauthenticatedAppException("Unauthenticated App found", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(decAppSessionID);
		
		String[] decryptedData = iEncryptionUtil.decrypt(appSessionKey, encRequestAuthenticator, encUserSessionID);
		String requestAuthenticatorStr = decryptedData[0];
		String userSessionID = decryptedData[1];
		
		if (iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			throw new UnauthenticatedAppException("Unauthenticated Request found", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		UserSession userSession = appServiceSession.findUserSessionBySessionID(userSessionID);
		if (userSession == null){
			throw new UnauthenticatedUserException("Unauthenticated User found", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		
		Date responseAuthenticator = iDateUtil.createResponseAuthenticator(requestAuthenticator);
		
		//Adding authenticators to session
		appServiceSession.addAuthenticator(requestAuthenticator);
		userSession.addAuthenticator(requestAuthenticator);
		userSession.addAuthenticator(responseAuthenticator);
		appServiceSession.addAuthenticator(responseAuthenticator);
		
		SecretKey userSessionKey = iEncryptionUtil.generateSecretKey(userSessionID);
		
		String encResponseAuthenticator = iEncryptionUtil.encrypt(userSessionKey, iDateUtil.generateStringFromDate(responseAuthenticator))[0];
		Map<String, String> encResponseData = null;
		if (responseData != null && responseData.keySet().size() > 0){
			encResponseData = new HashMap<>();
			Iterator<String> iterator = responseData.keySet().iterator();
			String key = null;
			while(iterator.hasNext()){
				key = iterator.next();
				encResponseData.put(key, iEncryptionUtil.encrypt(userSessionKey, responseData.get(key))[0]);
			}
		}
		
		AccessServiceResponse response = new AccessServiceResponse();
		response.setData(encResponseData);
		response.setEncAuthenticator(encResponseAuthenticator);
		
		
		return response;
	}
}