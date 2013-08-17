package com.device.service.rest.api;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.device.applicationdetailservice.ApplicationDetailService;
import com.device.exception.InvalidResponseAuthenticatorException;
import com.device.exception.ResponseDecryptionException;
import com.device.exception.RestClientException;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;
import com.device.service.rest.representation.UserAccessServiceRequest;
import com.device.service.rest.representation.UserAccessServiceResponse;
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
	public UserAccessServiceResponse generateAccessRequest(String url, RequestMethod requestMethod, ContentType contentType, String appID, String appSessionID, String requestAuthenticator, String userServiceSessionID, 
			Map<String, String> data, SecretKey kerberosAppServiceSessionKey, SecretKey appSessionKey, SecretKey userSessionKey) throws IOException, RestClientException{
		
		log.debug("Entering generateAccessRequest");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(url, appID, appSessionID, requestAuthenticator, userServiceSessionID) || kerberosAppServiceSessionKey == null || appSessionKey == null || userSessionKey == null){
			log.error("Invalid input parameter provided to generateAccessRequest");
			return null;
		}
		
		String encAppSessionID = iEncryptionUtil.encrypt(kerberosAppServiceSessionKey, appSessionID)[0];
		String[] encryptedData = iEncryptionUtil.encrypt(appSessionKey, requestAuthenticator, userServiceSessionID);
		String encRequestAuthenticator = encryptedData[0];
		String encUserServiceSessionID = encryptedData[1];
		
		Map<String, String> encData = iEncryptionUtil.encrypt(userSessionKey, data);
		
		UserAccessServiceRequest userAccessServiceRequest = new UserAccessServiceRequest();
		userAccessServiceRequest.setAppID(appID);
		userAccessServiceRequest.setData(encData);
		userAccessServiceRequest.setEncAppSessionID(encAppSessionID);
		userAccessServiceRequest.setEncAuthenticator(encRequestAuthenticator);
		userAccessServiceRequest.setEncUserSessionID(encUserServiceSessionID);
		
		UserAccessServiceResponse response = (UserAccessServiceResponse) iConnectionManager.generateRequest(url, requestMethod, contentType, UserAccessServiceResponse.class, iConnectionManager.generateJSONStringForObject(userAccessServiceRequest));
		
		log.debug("Returning from generateAccessRequest method");
		
		return response;
	}
	
	@Override
	public Map<String, String> processAccessResponse(String requestAuthenticatorStr, String encResponseAuthenticator, Map<String, String> encData, 
			AppSession appSession, UserSession userSession, SecretKey userSessionKey) throws ResponseDecryptionException, InvalidResponseAuthenticatorException {
		
		log.debug("Entering processAccessResponse method");
		
		if (encData == null || encData.size() == 0){
			return encData;
		}
		
		if (!iEncryptionUtil.validateDecryptedAttributes(encResponseAuthenticator) || userSessionKey == null){
			log.error("Invalid input parameter provided to processAccessResponse method");
			return null;
		}
		
		String responseAuthenticatorStr = iEncryptionUtil.decrypt(userSessionKey, encResponseAuthenticator)[0];
		if (!iEncryptionUtil.validateDecryptedAttributes(responseAuthenticatorStr)){
			log.error("Response Data failed to decrypt. Unauthorized response found");
			throw new ResponseDecryptionException(UserAccessServiceResponse.class, "processAccessResponse", this.getClass());
		}
		
		Date responseAuthenticator = iDateUtil.generateDateFromString(responseAuthenticatorStr);
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		if (!iDateUtil.validateAuthenticator(responseAuthenticator, requestAuthenticator)){
			log.error("Authenticator failed to validate. Unauthorized response found");
			throw new InvalidResponseAuthenticatorException(UserAccessServiceResponse.class, "processAccessResponse", this.getClass());
		}
		
		appSession.addAuthenticator(requestAuthenticator);
		appSession.addAuthenticator(responseAuthenticator);
		
		userSession.addAuthenticator(requestAuthenticator);
		userSession.addAuthenticator(responseAuthenticator);
		
		Map<String, String> data  = iEncryptionUtil.decrypt(userSessionKey, encData);
		return data;
	}
}





