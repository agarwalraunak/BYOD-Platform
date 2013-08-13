/**
 * 
 */
package com.device.service.rest.client;

import java.io.IOException;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.device.applicationdetailservice.ApplicationDetailService;
import com.device.kerberos.model.ServiceTicket;
import com.device.rest.exceptions.UnauthorizedRequestException;
import com.device.rest.exceptions.UnauthorizedResponseException;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;
import com.device.service.rest.api.IAccessServiceAPI;
import com.device.service.rest.representation.AccessServiceResponse;
import com.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.device.util.dateutil.IDateUtil;
import com.device.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */
@Component
public class AccessServiceClient {
	
	private static Logger log = Logger.getLogger(AccessServiceClient.class);
	
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IAccessServiceAPI iClientAccessServiceAPI;
	private @Autowired ApplicationDetailService applicationDetailService;
	

	/**
	 * @param url
	 * @param requestMethod
	 * @param contentType
	 * @param serviceTicket
	 * @param username
	 * @param data
	 * @return data sent by the Service
	 * @throws InvalidAttributeValueException
	 * @throws IOException
	 * @throws UnauthorizedResponseException 
	 * @throws UnauthorizedRequestException 
	 */
	public Map<String, String> accessService(String url, RequestMethod requestMethod, ContentType contentType, ServiceTicket serviceTicket, String username, Map<String, String> data) throws InvalidAttributeValueException, IOException, UnauthorizedResponseException, UnauthorizedRequestException{
		
		log.debug("Entering accessService method");
		
		AppSession appSession = serviceTicket.getAppServiceSession();
		UserSession userSession = appSession.findUserServiceSessionByUsername(username);
		if (userSession == null) {
			log.error("Service Session for User " +username +" does not exist");
			throw new UnauthorizedRequestException("Service Session for User " +username +" does not exist");
		}
		
		String appID = applicationDetailService.getAppLoginName();
		String kerberosAppServiceSessionID = serviceTicket.getServiceSessionID();
		String appSessionID = appSession.getSessionID();
		String  userServiceSessionID = userSession.getUserSessionID();
		String requestAuthenticator = iDateUtil.generateStringFromDate(userSession.createAuthenticator());
		
		if (!iEncryptionUtil.validateDecryptedAttributes(appID, appSessionID, userServiceSessionID)){
			log.error("Invalid Access Request from unauthenticated App and User");
			throw new IOException("Invalid Access Request from unauthenticated App and User");
		}
		
		SecretKey kerberosAppServiceSessionKey = iEncryptionUtil.generateSecretKey(kerberosAppServiceSessionID);
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSessionID);
		SecretKey userSessionKey = iEncryptionUtil.generateSecretKey(userServiceSessionID);
		
		AccessServiceResponse response = iClientAccessServiceAPI.generateAccessRequest(url, requestMethod, contentType, appID, appSessionID, requestAuthenticator, userServiceSessionID, data, kerberosAppServiceSessionKey, appSessionKey, userSessionKey);
		
		if (response == null){
			log.error("Service did not return any response");
			throw new IOException("Service did not return any response");
		}
		
		return iClientAccessServiceAPI.processAccessResponse(requestAuthenticator, response.getEncAuthenticator(), response.getData(), appSession, userSession, userSessionKey);
		
	}

}
