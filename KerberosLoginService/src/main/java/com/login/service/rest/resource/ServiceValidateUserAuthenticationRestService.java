/**
 * 
 */
package com.login.service.rest.resource;

import javax.crypto.SecretKey;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.exception.common.AuthenticatorValidationException;
import com.login.exception.common.InvalidRequestException;
import com.login.exception.common.UnauthenticatedAppException;
import com.login.model.SessionDirectory;
import com.login.service.rest.representation.ServiceValidateUserAuthenticationRequest;
import com.login.service.rest.representation.ServiceValidateUserAuthenticationResponse;
import com.login.util.dateutil.IDateUtil;
import com.login.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */
@Component
@Path("/validate")
public class ServiceValidateUserAuthenticationRestService {
	
	private static Logger log = Logger.getLogger(ServiceValidateUserAuthenticationRestService.class);
	
	private @Autowired SessionDirectory sessionDirectory;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	
	@Path("/user/authentication")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ServiceValidateUserAuthenticationResponse validateUserAuthentication(ServiceValidateUserAuthenticationRequest request) throws InvalidRequestException, UnauthenticatedAppException, AuthenticatorValidationException{
		
		log.debug("Entering validateUserAuthentication method");
//		
//		String appLoginName = request.getAppID();
//		String encAuthenticator = request.getEncAuthenticator();
//		String encUserLoginSessionID = request.getEncUserLoginSessionID();
//		
//		if (!iEncryptionUtil.validateDecryptedAttributes(appLoginName, encAuthenticator, encUserLoginSessionID)){
//			log.error("Invalid Request Attributes, Bad Request Found!");
//			throw new InvalidRequestException(); 
//		}
//		
//		//Find App Session for the given App Name
//		AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(appLoginName);
//		if (appSession == null){
//			log.error("Request from Unauthenticated App Found");
//			throw new UnauthenticatedAppException();
//		}
//		
//		//Decrypting the request parameters
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(request.getEncAppSessionID());
//		String[] decryptedData = iEncryptionUtil.decrypt(appSessionKey, encAuthenticator, encUserLoginSessionID);
//		
//		//validate the decrypted attributes
//		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
//			log.error("Invalid Request Attributes, Unable to decrypt the request parameters");
//			throw new UnauthenticatedAppException();
//		}
//		
//		String requestAuthenticatorStr = decryptedData[0];
//		String userLoginSessionID = decryptedData[1];
//		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
//		
//		//Validate the authenticator
//		if (!appSession.validateAuthenticator(requestAuthenticator)){
//			log.error("Invalid Request Authenticator found, Request Denied");
//			throw new AuthenticatorValidationException();
//		}
		
		//Creating Response Attributes
		Boolean isAuthenticated = true;
		if (sessionDirectory.findActiveUserSessionBySessionID(request.getEncUserLoginSessionID()) == null){
			isAuthenticated = false;
		}
//		Date responseAuthenticator = iDateUtil.createResponseAuthenticator(requestAuthenticator);
//		String responseAuthenticatorStr = iDateUtil.generateStringFromDate(responseAuthenticator);
		
		
		//Creating the response
		ServiceValidateUserAuthenticationResponse response = new ServiceValidateUserAuthenticationResponse();
		response.setEncIsAuthenticated(isAuthenticated.toString());
		
		log.debug("Returning from validateUserAuthentication");
		
		return response;
		
	}

}
