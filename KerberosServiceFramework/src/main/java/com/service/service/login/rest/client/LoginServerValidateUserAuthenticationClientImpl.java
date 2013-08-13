/**
 * 
 */
package com.service.service.login.rest.client;

import java.io.IOException;
import java.util.Date;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.config.KerberosURLConfig;
import com.service.config.applicationdetailservice.ApplicationDetailService;
import com.service.model.service.ServiceSession;
import com.service.rest.exception.common.InternalSystemException;
import com.service.service.login.rest.representation.ServiceValidateUserAuthenticationRequest;
import com.service.service.login.rest.representation.ServiceValidateUserAuthenticationResponse;
import com.service.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.service.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.service.util.connectionmanager.IConnectionManager;
import com.service.util.dateutil.IDateUtil;
import com.service.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */
@Component
public class LoginServerValidateUserAuthenticationClientImpl implements ILoginServerValidateUserAuthenticationClient {
	
	private static Logger log = Logger.getLogger(LoginServerValidateUserAuthenticationClientImpl.class);
	
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	private @Autowired ApplicationDetailService applicationDetailService;
	private @Autowired IConnectionManager iConnectionManager;
	private @Autowired KerberosURLConfig kerberosURLConfig;

	@Override
	public boolean validateUserAuthenticationAgainstLoginServer(ServiceSession appLoginServerSession, String userLoginServiceSession) throws InternalSystemException {
		
		log.debug("Entering validateUserAuthenticationAgainstLoginServer");
		
		if (appLoginServerSession == null || userLoginServiceSession == null || userLoginServiceSession.isEmpty()){
			log.error("Invalid Input parameter provided to validateUserAuthenticationAgainstLoginServer");
			return false;
		}
		
		//Generating Request Authenticator
		Date requestAuthenticator = appLoginServerSession.createAuthenticator();
		String requestAuthenticatorStr = iDateUtil.generateStringFromDate(requestAuthenticator);
		
		//Generating secret key using appLoginSessionID
		String appLoginSessionID = appLoginServerSession.getSessionID();
		SecretKey appLoginSessionKey = iEncryptionUtil.generateSecretKey(appLoginSessionID); 
		
		//Encrypting Request Attributess
		String[] encryptedData = iEncryptionUtil.encrypt(appLoginSessionKey, requestAuthenticatorStr, userLoginServiceSession);
		
		//Creating Request
		ServiceValidateUserAuthenticationRequest request = new ServiceValidateUserAuthenticationRequest();
		request.setAppID(applicationDetailService.getAppLoginName());
		request.setEncAuthenticator(encryptedData[0]);
		request.setEncUserLoginSessionID(encryptedData[1]);
		
		//Requesting Login Server
		ServiceValidateUserAuthenticationResponse response;
		try {
			response = (ServiceValidateUserAuthenticationResponse)iConnectionManager.generateRequest(
					kerberosURLConfig.getLOGIN_SERVER_USER_AUTHENTICATION_URL(), 
					RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, 
					ServiceValidateUserAuthenticationResponse.class, iConnectionManager.generateJSONStringForObject(request));
		} catch (IOException e) {
			log.error("Failed to validate user login against Login Server");
			e.printStackTrace();
			throw new InternalSystemException();
		}

		//Decrypting the response
		String[] decryptedData = iEncryptionUtil.decrypt(appLoginSessionKey, response.getEncIsAuthenticated(), response.getEncResponseAuthenticator());
		String isAuthenticated = decryptedData[0];
		String responseAuthenticatorStr = decryptedData[1];
		
		//Validating the response parameters
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			log.error("Invalid Response from Login Server Found. Validation of User Authentication Failed!");
			throw new InternalSystemException();
		}
		
		//Validate the response authenticator
		Date responseAuthenticator = iDateUtil.generateDateFromString(responseAuthenticatorStr);
		if(!iDateUtil.validateAuthenticator(responseAuthenticator, requestAuthenticator)){
			log.error("Invalid Response from Login Server Found. Validation of User Authentication Failed!");
			throw new InternalSystemException();
		}
		
		//Storing the authenticators
		appLoginServerSession.addAuthenticator(requestAuthenticator);
		appLoginServerSession.addAuthenticator(responseAuthenticator);
		
		return Boolean.parseBoolean(isAuthenticated);
		
	}
	
}