/**
 * 
 */
package com.service.service.rest.api;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.config.applicationdetailservice.ApplicationDetailService;
import com.service.exception.common.AuthenticatorValidationException;
import com.service.model.SessionDirectory;
import com.service.model.kerberos.KerberosAppSession;
import com.service.model.service.ServiceSession;
import com.service.service.rest.representation.AppAccessServiceRequest;
import com.service.service.rest.representation.AppAccessServiceResponse;
import com.service.util.dateutil.IDateUtil;
import com.service.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */
@Component
public class AppAccessServiceAPIImpl implements IAppAccessServiceAPI{

	private static Logger log = Logger.getLogger(AppAccessServiceAPIImpl.class);
	
	private @Autowired ApplicationDetailService applicationDetailService;
	private @Autowired SessionDirectory sessionDirectory;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	
	@Override
	public AppAccessServiceRequest generateAppAccessServiceRequest(KerberosAppSession kerberosAppSession, String serviceSessionID, ServiceSession serviceSession,
			Date requestAuthenticator, Map<String, String> requestData){
		
		log.debug("Entering generateAppAccessServiceRequest");
		
		if (kerberosAppSession == null || serviceSession== null || serviceSessionID == null || requestAuthenticator == null){
			return null;
		}
		
		//Get the Service Session from the service ticket
		String sessionID = serviceSession.getSessionID();
		
		//Creating request authenticator
		String requestAuthenticatorStr = iDateUtil.generateStringFromDate(requestAuthenticator);
		
		//Generating Required Secret Keys
		SecretKey sessionKey = iEncryptionUtil.generateSecretKey(sessionID);
		SecretKey serviceSessionKey = iEncryptionUtil.generateSecretKey(serviceSessionID);
		
		//Encrypting Request Attributes
		String encSessionID = iEncryptionUtil.encrypt(serviceSessionKey, sessionID)[0];
		String encRequestAuthenticator = iEncryptionUtil.encrypt(sessionKey, requestAuthenticatorStr)[0];
		Map<String, String> encData = iEncryptionUtil.encrypt(sessionKey, requestData);
		
		//Creating the Request
		AppAccessServiceRequest request = new AppAccessServiceRequest();
		request.setEncAppSessionID(encSessionID);
		request.setEncAuthenticator(encRequestAuthenticator);
		request.setData(encData);
		request.setAppID(applicationDetailService.getAppLoginName());
		
		log.debug("Returning from generateAppAccessServiceRequest");
		
		return request;
	}
	
	@Override
	public Map<String, String> processAppAccessServiceResponse(AppAccessServiceResponse response, Date requestAuthenticator, ServiceSession serviceSession) throws AuthenticatorValidationException{
		
		log.debug("Entering processAppAccessServiceResponse");
		
		if (response == null || requestAuthenticator == null || serviceSession == null){
			log.error("Invalid method argument to processAppAccessServiceResponse");
			return null;
		}
		
		String encResponseAuthenticator = response.getEncResponseAuthenticator();
		Map<String, String> encResponseData = response.getEncResponseData();
		
		//Generating key from Service Application Session ID
		SecretKey sessionKey = iEncryptionUtil.generateSecretKey(serviceSession.getSessionID());
		
		//Decrypting the response authenticator
		String responseAuthenticatorStr = iEncryptionUtil.decrypt(sessionKey, encResponseAuthenticator)[0];
		Date responseAuthenticator = iDateUtil.generateDateFromString(responseAuthenticatorStr);
		
		//Validating the authenticator
		if (!iDateUtil.validateAuthenticator(responseAuthenticator, requestAuthenticator)){
			throw new AuthenticatorValidationException();
		}
		
		//Adding the authenticator to service session
		serviceSession.addAuthenticator(requestAuthenticator);
		serviceSession.addAuthenticator(responseAuthenticator);
		
		//Decrypting the response data
		Map<String, String> decResponseData = iEncryptionUtil.decrypt(sessionKey, encResponseData);
		
		log.debug("Returning from processAppAccessServiceResponse");
		
		return decResponseData;
	}
	
}
