/**
 * 
 */
package com.kerberos.service.rest.api.app;

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

import com.kerberos.rest.representation.device.AccessServiceRequest;
import com.kerberos.rest.representation.device.AccessServiceResponse;
import com.kerberos.rest.representation.kerberos.KeyServerResponse;
import com.kerberos.service.models.AppSession;
import com.kerberos.service.models.AppSessionDirectory;
import com.kerberos.service.models.UserSession;
import com.kerberos.service.rest.api.kerberos.KerberosAuthenticationAPIImpl.SecretKeyType;
import com.kerberos.service.rest.exceptions.InvalidRequestException;
import com.kerberos.service.rest.exceptions.ServiceUnavailableException;
import com.kerberos.service.util.dateutil.IDateUtil;
import com.kerberos.service.util.encryption.IEncryptionUtil;
import com.kerberos.service.util.hashing.IHashUtil;

/**
 * @author raunak
 *
 */
public class AccessServiceAPIImpl implements IAccessServiceAPI{
	
	private static Logger log = Logger.getLogger(AccessServiceAPIImpl.class);

	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IHashUtil iHashUtil;
	private @Autowired AppSessionDirectory appSessionDirectory;
	
	
	@Override
	public KeyServerResponse processKeyServerResponse(KeyServerResponse response, Date requestAuthenticator, SecretKey serviceSessionKey) throws InvalidAttributeValueException{
		
		log.debug("Entering processResponse method");
		
		if (response == null || requestAuthenticator == null || serviceSessionKey == null){
			log.error("Invalid input parameter to processResponse");
			throw new InvalidAttributeValueException("Invalid input parameter to processResponse");
		}
		
		String responseAuthenticatorStr = iEncryptionUtil.decrypt(serviceSessionKey, response.getEncResponseAuthenticator())[0];
		Date responseAuthenticator = iDateUtil.generateDateFromString(responseAuthenticatorStr);
		
		if (!iDateUtil.validateAuthenticator(responseAuthenticator, requestAuthenticator)){
			return null;
		}
		
		Map<String, String> encResponseData = response.getResponseData();
		if (encResponseData == null){
			return null;
		}
		
		Map<String, String> responseData = new HashMap<String, String>();
		
		Iterator<String> iterator = encResponseData.keySet().iterator();
		String key = null;
		while(iterator.hasNext()){
			key = iterator.next();
			responseData.put(key, iEncryptionUtil.decrypt(serviceSessionKey, encResponseData.get(key))[0]);
		}
		
		response.setResponseData(responseData);
		
		return response;
		
	}
	
	@Override 
	public SecretKey getKeyFromResponseData(Map<String, String> responseData, SecretKey serviceSessionKey, SecretKeyType keyType) throws InvalidAttributeValueException, ServiceUnavailableException{
		
		log.debug("Entering getKeyFromResponseData method");
		
		if (responseData == null || serviceSessionKey == null || keyType == null){
			log.error("Invalid parameter provided in getKeyFromResponseData");
			throw new InvalidAttributeValueException("Invalid parameter provided in getKeyFromResponseData");
		}
		
		String serviceKeyStr = responseData.get(keyType.getValue());
		if (serviceKeyStr == null){
			log.error("Unable to get the key from key server");
			throw new ServiceUnavailableException("Error processing the request. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		String decServiceKeyStr = null;
		try {
			decServiceKeyStr = iEncryptionUtil.decrypt(serviceSessionKey, serviceKeyStr)[0];
		} catch (InvalidAttributeValueException e) {
			log.error("Error processing the request. Detailed Exception is attached below: \n"+e.getMessage());
			e.printStackTrace();
			throw new ServiceUnavailableException("Error processing the request. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		if(decServiceKeyStr == null){
			log.error("Error processing the request");
			throw new ServiceUnavailableException("Error processing the request. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		SecretKey serviceKey = iEncryptionUtil.generateSecretKeyFromBytes(iHashUtil.stringToByte(decServiceKeyStr));
		
		return serviceKey;
	}

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
		
		AppSession appServiceSession = appSessionDirectory.findAppSessionByAppID(appID);
		
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
		
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		UserSession userSession = appServiceSession.findUserSessionBySessionID(userSessionID);
		if (userSession == null || !userSession.validateAuthenticator(requestAuthenticator)){
			log.error("User Session ID does not exist. Request Invalid!");
			throw new InvalidRequestException("User Session ID does not exist. Request Invalid!", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
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
		
		AppSession appServiceSession = appSessionDirectory.findAppSessionByAppID(appID);
		
		SecretKey appServiceSessionKey = iEncryptionUtil.generateSecretKey(appServiceSession.getAppServiceSessionID());
		
		String decAppSessionID = iEncryptionUtil.decrypt(appServiceSessionKey, encAppSessionID)[0];
		
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(decAppSessionID);
		
		String[] decryptedData = iEncryptionUtil.decrypt(appSessionKey, encRequestAuthenticator, encUserSessionID);
		String requestAuthenticatorStr = decryptedData[0];
		String userSessionID = decryptedData[1];
		
		UserSession userSession = appServiceSession.findUserSessionBySessionID(userSessionID);
		
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		
		Date responseAuthenticator = iDateUtil.createResponseAuthenticator(requestAuthenticator);
		
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