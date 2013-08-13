/**
 * 
 */
package com.login.kerberos.rest.api;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.kerberos.rest.representation.AuthenticationResponse;
import com.login.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.login.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.login.util.connectionmanager.IConnectionManager;
import com.login.util.dateutil.IDateUtil;
import com.login.util.encryption.IEncryptionUtil;
import com.login.util.hashing.HashUtilImpl.HashingTechqniue;
import com.login.util.hashing.IHashUtil;

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
	public Map<AuthenticationResponseAttributes, String> authenticate(String url, String loginName, String password, boolean isApplication) throws IOException, NoSuchAlgorithmException, InvalidAttributeValueException{
		
		if (!iEncryptionUtil.validateDecryptedAttributes(url, loginName, password)){
			throw new InvalidAttributeValueException("Invalid input parameters to authenticate method");
		}
		
		String authenticationUrl = new StringBuilder(url).append(loginName).toString();
		
		AuthenticationResponse response = (AuthenticationResponse) iConnectionManager.generateRequest(authenticationUrl, RequestMethod.GET_REQUEST_METHOD, ContentType.TEXT_HTML, AuthenticationResponse.class);
		
		if (response == null){
			return null;
		}
		
		return processAuthenticationResponse(loginName, password, response);
	}

	@Override
	public SecretKey generatePasswordSymmetricKey(String loginAppName, String appPassword) throws NoSuchAlgorithmException, InvalidAttributeValueException{
		
		log.debug("Entering generatePasswordSymmetricKey method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(loginAppName, appPassword)){
			log.error("Invalid Input parameter to generatePasswordSymmetricKey");
			throw new InvalidAttributeValueException("Invalid Input parameter to generatePasswordSymmetricKey");
		}
		
		byte[] hashedPasswordPhraseBytes = iHashUtil.getHashWithSalt(appPassword, HashingTechqniue.SSHA256, iHashUtil.stringToByte(loginAppName));
		String hashPasswordPhrase = iHashUtil.bytetoString(hashedPasswordPhraseBytes);
		SecretKey passwordSymmetricKey = iEncryptionUtil.generateSecretKey(hashPasswordPhrase);

		log.debug("Returning from generatePasswordSymmetricKey method");
		
		return passwordSymmetricKey;
	}
	
	public Map<AuthenticationResponseAttributes, String> decryptAuthenticationResponse(AuthenticationResponse response, SecretKey key) throws InvalidAttributeValueException{
		
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
	
	public Map<AuthenticationResponseAttributes, String> processAuthenticationResponse(String loginAppName, String appPassword, AuthenticationResponse response) throws NoSuchAlgorithmException, InvalidAttributeValueException{

		log.debug("Entering processAuthenticationResponse method");
		
		if(!iEncryptionUtil.validateDecryptedAttributes(loginAppName, appPassword) || response == null){
			log.debug("Invalid input parameter processAuthenticationResponse method");
			throw new InvalidAttributeValueException("Invalid input parameter to processAuthenticationResponse");
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
