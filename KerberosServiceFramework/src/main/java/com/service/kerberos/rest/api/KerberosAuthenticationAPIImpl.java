/**
 * 
 */
package com.service.kerberos.rest.api;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.kerberos.rest.representation.AuthenticationResponse;
import com.service.rest.exception.common.InternalSystemException;
import com.service.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.service.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.service.util.connectionmanager.IConnectionManager;
import com.service.util.dateutil.IDateUtil;
import com.service.util.encryption.IEncryptionUtil;
import com.service.util.hashing.HashUtilImpl.HashingTechqniue;
import com.service.util.hashing.IHashUtil;

/**
 * @author raunak
 *
 */
@Component
public class KerberosAuthenticationAPIImpl implements IKerberosAuthenticationAPI{
	
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IHashUtil iHashUtil;
	private @Autowired IConnectionManager iConnectionManager;
	private @Autowired IDateUtil iDateUtil;
	
	private static Logger log = Logger.getLogger(KerberosAuthenticationAPIImpl.class);
	
	public enum AuthenticationResponseAttributes{
		TGT_PACKET, SESSION_KEY, LOGIN_NAME;
	}
	public enum ServiceTicketResponseAttributes{
		SERVICE_TICKET_PACKET, SERVICE_SESSION_ID, SERVICE_NAME, AUTHENTICATOR, EXPIRY_TIME;
	}
	
	public enum SecretKeyType{
		SESSION_MANAGEMENT_KEY("SESSION_MANAGEMENT_KEY"), SERVICE_KEY("SERVICE_KEY"), KEY_SERVER("KEY_SERVER");
		
		private String value;
		
		SecretKeyType(String value){
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}	
	
	@Override
	public Map<AuthenticationResponseAttributes, String> authenticate(String url, String loginName, String password, boolean isApplication) throws InternalSystemException{
		
		log.debug("Entering authenticate");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(url, loginName, password)){
			return null;
		}
		
		String authenticationUrl = new StringBuilder(url).append(loginName).toString();
		
		AuthenticationResponse response;
		try {
			response = (AuthenticationResponse) iConnectionManager.generateRequest(authenticationUrl, RequestMethod.GET_REQUEST_METHOD, ContentType.TEXT_HTML, AuthenticationResponse.class);
		} catch (IOException e) {
			log.error("Error authenticating Service Application against kerberos");
			e.printStackTrace();
			throw new InternalSystemException();
		}
		
		if (response == null){
			return null;
		}
		
		return processAuthenticationResponse(loginName, password, response);
	}

	@Override
	public SecretKey generatePasswordSymmetricKey(String loginAppName, String appPassword) throws InternalSystemException {
		
		log.debug("Entering generatePasswordSymmetricKey method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(loginAppName, appPassword)){
			log.error("Invalid Input parameter to generatePasswordSymmetricKey");
			return null;
		}
		
		byte[] hashedPasswordPhraseBytes = null;
		try {
			hashedPasswordPhraseBytes = iHashUtil.getHashWithSalt(appPassword, HashingTechqniue.SSHA256, iHashUtil.stringToByte(loginAppName));
		} catch (NoSuchAlgorithmException e) {
			log.error("Error generating Password Symmetric Key\n"+e.getMessage());
			e.printStackTrace();
			throw new InternalSystemException();
		}
		String hashPasswordPhrase = iHashUtil.bytetoString(hashedPasswordPhraseBytes);
		SecretKey passwordSymmetricKey = iEncryptionUtil.generateSecretKey(hashPasswordPhrase);

		log.debug("Returning from generatePasswordSymmetricKey method");
		
		return passwordSymmetricKey;
	}
	
	public Map<AuthenticationResponseAttributes, String> decryptAuthenticationResponse(AuthenticationResponse response, SecretKey key) {
		
		log.debug("Entering decryptAuthenticationResponse");
		
		String[] decryptedData = iEncryptionUtil.decrypt(key, response.getEncTgtPacket(), response.getEncSessionKey(), response.getEncLoginName());
		if (decryptedData == null){
			return null;
		}
		String tgtPacket = decryptedData[0];
		String sessionKey = decryptedData[1];
		String loginName = decryptedData[2];
		
		Map<AuthenticationResponseAttributes, String> responseAttributes = new HashMap<AuthenticationResponseAttributes, String>();
		
		responseAttributes.put(AuthenticationResponseAttributes.LOGIN_NAME, loginName);
		responseAttributes.put(AuthenticationResponseAttributes.SESSION_KEY, sessionKey);
		responseAttributes.put(AuthenticationResponseAttributes.TGT_PACKET, tgtPacket);
			
		log.debug("Returning from decryptAuthenticationResponse"); 
		
		return responseAttributes;
	}
	
	public Map<AuthenticationResponseAttributes, String> processAuthenticationResponse(String loginAppName, String appPassword, AuthenticationResponse response) throws InternalSystemException {

		log.debug("Entering processAuthenticationResponse method");
		
		if(!iEncryptionUtil.validateDecryptedAttributes(loginAppName, appPassword) || response == null){
			log.debug("Invalid input parameter processAuthenticationResponse method");
			return null;
		}
		
		SecretKey passwordSymmetricKey = generatePasswordSymmetricKey(loginAppName, appPassword);
		if (passwordSymmetricKey == null){
			return null;
		}
		
		Map<AuthenticationResponseAttributes, String> responseAttributes = decryptAuthenticationResponse(response, passwordSymmetricKey);
		if(responseAttributes == null){
			return null;
		}
		
		String loginName = responseAttributes.get(AuthenticationResponseAttributes.LOGIN_NAME);
		String sessionKey = responseAttributes.get(AuthenticationResponseAttributes.SESSION_KEY);
		String tgtPacket = responseAttributes.get(AuthenticationResponseAttributes.TGT_PACKET);
		
		//if the Decryption is not valid App authentication fails!
		if (!iEncryptionUtil.validateDecryptedAttributes(loginName, sessionKey, tgtPacket)){
			return null;
		}
		
		log.debug("Returning from processAuthenticationResponse");
		
		return responseAttributes;
	}
	
	
}
