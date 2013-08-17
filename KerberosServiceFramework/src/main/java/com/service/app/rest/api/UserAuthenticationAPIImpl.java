/**
 * 
 */
package com.service.app.rest.api;

import java.util.Date;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.app.rest.representation.UserServiceAuthenticationRequest;
import com.service.app.rest.representation.UserServiceAuthenticationResponse;
import com.service.exception.common.AuthenticatorValidationException;
import com.service.exception.common.InvalidRequestException;
import com.service.model.app.AppSession;
import com.service.util.dateutil.IDateUtil;
import com.service.util.encryption.IEncryptionUtil;
import com.service.util.hashing.IHashUtil;

/**
 * @author raunak
 *
 */
@Component
public class UserAuthenticationAPIImpl implements IUserAuthenticationAPI {

	private static Logger log = Logger.getLogger(UserAuthenticationAPIImpl.class);
	
	private @Autowired IEncryptionUtil iEncryptionUtil; 
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IHashUtil iHashUtil;
	
	@Override
	public String[] decrytAndValidateUserServiceAuthenticationRequestParameters(AppSession appSession, SecretKey appSessionKey, UserServiceAuthenticationRequest request) throws AuthenticatorValidationException, InvalidRequestException {
		
		log.debug("Entering decrytAndValidateUserServiceAuthenticationRequestParameters");
		
		//valdiate the input parameters
		if (appSession == null || appSessionKey == null || request == null){
			log.error("Invalid Input parameters to decrytAndValidateUserServiceAuthenticationRequestParameters");
			throw new InvalidRequestException();
		}
		
		String encAuthenticator = request.getEncAuthenticator();
		String encUserSessionID = request.getEncUserSessionID();
		String encUsername = request.getEncUsername();
		
		//Decrypt the parameters
		String[] decryptedData = iEncryptionUtil.decrypt(appSessionKey, encUsername, encUserSessionID, encAuthenticator);
		
		//Validate the decrypted attributes
		if (!iEncryptionUtil.validateDecryptedAttributes(decryptedData)){
			log.error("Bad Request Found, Request parameters failed to decrypt");
			throw new InvalidRequestException();
		}
		
		String requestAuthenticatorStr = decryptedData[2];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		
		//Validate the Authenticator
		if (!appSession.validateAuthenticator(requestAuthenticator)){
			log.error("Unauthorized request authenticator failed to validate");
			throw new AuthenticatorValidationException();
		}
		
		log.debug("Returning from decrytAndValidateUserServiceAuthenticationRequestParameters");
		
		return decryptedData;
	}

	@Override
	public UserServiceAuthenticationResponse generateUserServiceAuthenticationResponse(Date requestAuthenticator, String userSessionID, SecretKey appSessionKey, AppSession appSession) {
		
		log.debug("Entering generateUserServiceAuthenticationResponse");
		
		if (requestAuthenticator == null || appSessionKey == null || appSession == null){
			log.error("Invalid input parameter provided to generateUserServiceAuthenticationResponse");
			return null;
		}
		
		//Creating Response Authenticator
		Date responseAuthenticator = iDateUtil.createResponseAuthenticator(requestAuthenticator);
		String responseAuthenticatorStr = iDateUtil.generateStringFromDate(responseAuthenticator);
		
		String[] encryptedData = null;
		String encResponseAuthenticator = null;
		String encUserSessionID = null;
		if (userSessionID != null){
			encryptedData = iEncryptionUtil.encrypt(appSessionKey, userSessionID, responseAuthenticatorStr);
			encUserSessionID = encryptedData[0];
			encResponseAuthenticator = encryptedData[1];
		}
		else{
			encryptedData = iEncryptionUtil.encrypt(appSessionKey, responseAuthenticatorStr);
			encResponseAuthenticator = encryptedData[0];
		}
		
		//If the user is not authenticated setting the user session id as null 
		UserServiceAuthenticationResponse response = new UserServiceAuthenticationResponse();
		response.setEncResponseAuthenticator(encResponseAuthenticator);
		response.setEncUserSessionID(encUserSessionID);
	
		appSession.addAuthenticator(requestAuthenticator);
		appSession.addAuthenticator(responseAuthenticator);
		
		
		return response;
	}
}
