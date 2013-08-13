/**
 * 
 */
package com.login.service.rest.resource;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.app.model.AppSession;
import com.login.app.model.AppSessionDirectory;
import com.login.rest.exceptions.InvalidRequestException;
import com.login.rest.exceptions.ServiceUnavailableException;
import com.login.rest.exceptions.UnauthenticatedAppException;
import com.login.rest.exceptions.UnauthenticatedRequestException;
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
	
	private @Autowired AppSessionDirectory appSessionDirectory;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	
	@Path("/user/authentication")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ServiceValidateUserAuthenticationResponse validateUserAuthentication(ServiceValidateUserAuthenticationRequest request){
		
		log.debug("Entering validateUserAuthentication method");
		
		String appLoginName = request.getAppID();
		String encAuthenticator = request.getEncAuthenticator();
		String encUserLoginSessionID = request.getEncUserLoginSessionID();
		
		if (!iEncryptionUtil.validateDecryptedAttributes(appLoginName, encAuthenticator, encUserLoginSessionID)){
			log.error("Invalid Request Attributes, Bad Request Found!");
			throw new InvalidRequestException("Invalid Request Attributes, Bad Request Found!", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML); 
		}
		
		//Find App Session for the given App Name
		AppSession appSession = appSessionDirectory.findAppSessionByAppID(appLoginName);
		if (appSession == null){
			log.error("Request from Unauthenticated App Found");
			throw new UnauthenticatedAppException("Request from Unauthenticated App Found", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		//Decrypting the request parameters
		SecretKey appSessionKey = null;
		String[] decryptedData = null;
		try {
			appSessionKey = iEncryptionUtil.generateSecretKey(appSession.getAppSessionID());
			decryptedData = iEncryptionUtil.decrypt(appSessionKey, encAuthenticator, encUserLoginSessionID);
		} catch (InvalidAttributeValueException e) {
			log.error(e);
			e.printStackTrace();
			throw new ServiceUnavailableException("Internal Server Error, Please try again later!", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		//validate the decrypted attributes
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			log.error("Invalid Request Attributes, Unable to decrypt the request parameters");
			throw new UnauthenticatedRequestException("Unauthenticated Request Found", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML);
		}
		
		String requestAuthenticatorStr = decryptedData[0];
		String userLoginSessionID = decryptedData[1];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		
		//Validate the authenticator
		if (!appSession.validateAuthenticator(requestAuthenticator)){
			log.error("Invalid Request Authenticator found, Request Denied");
			throw new InvalidRequestException("Unauthorized request found", Response.Status.UNAUTHORIZED, MediaType.TEXT_HTML);
		}
		
		//Creating Response Attributes
		Boolean isAuthenticated = true;
		if (appSessionDirectory.findUserSessionBySessionID(userLoginSessionID) == null){
			isAuthenticated = false;
		}
		Date responseAuthenticator = iDateUtil.createResponseAuthenticator(requestAuthenticator);
		String responseAuthenticatorStr = iDateUtil.generateStringFromDate(responseAuthenticator);
		
		String[] encryptedData = null;
		try {
			encryptedData = iEncryptionUtil.encrypt(appSessionKey, responseAuthenticatorStr, isAuthenticated.toString());
		} catch (InvalidAttributeValueException e) {
			log.error(e);
			e.printStackTrace();
			throw new ServiceUnavailableException("Internal Server Error, Please try again later!", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		//Creating the response
		ServiceValidateUserAuthenticationResponse response = new ServiceValidateUserAuthenticationResponse();
		response.setEncIsAuthenticated(encryptedData[1]);
		response.setEncResponseAuthenticator(encryptedData[0]);
		
		log.debug("Returning from validateUserAuthentication");
		
		return response;
		
	}

}
