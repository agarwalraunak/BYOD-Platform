
/**
 * 
 */
package com.device.service.rest.client;

import java.io.IOException;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.device.applicationdetailservice.ApplicationDetailService;
import com.device.exception.InvalidResponseAuthenticatorException;
import com.device.exception.ResponseDecryptionException;
import com.device.exception.RestClientException;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;
import com.device.service.rest.api.IAccessServiceAPI;
import com.device.service.rest.representation.UserAccessServiceResponse;
import com.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.device.util.dateutil.IDateUtil;
import com.device.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */
@Component
public class AccessServiceClientImpl implements IAccessServiceClient{
	
	private static Logger log = Logger.getLogger(AccessServiceClientImpl.class);
	
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IAccessServiceAPI iClientAccessServiceAPI;
	private @Autowired ApplicationDetailService applicationDetailService;
	
	@Override
	public Map<String, String> accessService(String url, RequestMethod requestMethod, ContentType contentType, AppSession appSession, String kerberosServiceSessionID, 
			UserSession userSession, Map<String, String> data) throws  IOException, RestClientException, 
			ResponseDecryptionException, InvalidResponseAuthenticatorException{
		
		log.debug("Entering accessService method");
		
		String appID = applicationDetailService.getAppLoginName();
		String appSessionID = appSession.getSessionID();
		String  userServiceSessionID = userSession.getUserSessionID();
		String requestAuthenticator = iDateUtil.generateStringFromDate(userSession.createAuthenticator());
		
		if (!iEncryptionUtil.validateDecryptedAttributes(appID, appSessionID, userServiceSessionID)){
			log.error("Invalid Access Request from unauthenticated App and User");
		}
		
		SecretKey kerberosAppServiceSessionKey = iEncryptionUtil.generateSecretKey(kerberosServiceSessionID);
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSessionID);
		SecretKey userSessionKey = iEncryptionUtil.generateSecretKey(userServiceSessionID);
		
		UserAccessServiceResponse response;
		//If Response status is UNAUTHORIZED deactivate userSession 
		try{
			response = iClientAccessServiceAPI.generateAccessRequest(url, requestMethod, contentType, appID, appSessionID, requestAuthenticator, userServiceSessionID, data, kerberosAppServiceSessionKey, appSessionKey, userSessionKey);
		}
		catch(RestClientException e){
			if (e.getErrorCode() == Response.Status.UNAUTHORIZED.getStatusCode()){
				userSession.setActive(false);
			}
			throw e;
		}
		if (response == null){
			log.error("Service did not return any response");
			throw new IOException("Service did not return any response");
		}
		
		return iClientAccessServiceAPI.processAccessResponse(requestAuthenticator, response.getEncAuthenticator(), response.getData(), appSession, userSession, userSessionKey);
		
	}

}
