package com.device.service.rest.api;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.device.applicationdetailservice.ApplicationDetailService;
import com.device.rest.exceptions.UnauthorizedResponseException;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;
import com.device.service.rest.representation.AccessServiceRequest;
import com.device.service.rest.representation.AccessServiceResponse;
import com.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.device.util.connectionmanager.IConnectionManager;
import com.device.util.dateutil.IDateUtil;
import com.device.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */

@Component
public class AccessServiceAPIImpl implements IAccessServiceAPI{

	private static Logger log = Logger.getLogger(AccessServiceAPIImpl.class);
	
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired ApplicationDetailService applicationDetailService;
	private @Autowired IConnectionManager iConnectionManager;
	
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
	public Map<String, String> processAccessResponse(String requestAuthenticatorStr, String encResponseAuthenticator, Map<String, String> encData, AppSession appSession, UserSession userSession, SecretKey userSessionKey) throws InvalidAttributeValueException, UnauthorizedResponseException{
		
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
		
		appSession.addAuthenticator(requestAuthenticator);
		appSession.addAuthenticator(responseAuthenticator);
		
		userSession.addAuthenticator(requestAuthenticator);
		userSession.addAuthenticator(responseAuthenticator);
		
		
		
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





