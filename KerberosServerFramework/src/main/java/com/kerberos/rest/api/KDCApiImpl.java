/**
 * 
 */
package com.kerberos.rest.api;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kerberos.ActiveDirectory.ActiveDirectoryImpl.SecretKeyType;
import com.kerberos.ActiveDirectory.IActiveDirectory;
import com.kerberos.configuration.KerberosConfigurationManager;
import com.kerberos.dateutil.IDateUtil;
import com.kerberos.db.model.TGT;
import com.kerberos.db.service.ITGTService;
import com.kerberos.encryption.IEncryptionUtil;
import com.kerberos.hashing.IHashUtil;
import com.kerberos.keyserver.KeyServerUtil;
import com.kerberos.rest.representation.AuthenticationResponse;

/**
 * @author raunak
 *
 */
public class KDCApiImpl implements IKDCApi{
	
	private static Logger log = Logger.getLogger(KDCApiImpl.class);
	
	private @Autowired IActiveDirectory activeDirectory;
	private @Autowired IDateUtil iDateUtil;
	private @Autowired KerberosConfigurationManager kerberosConfigurationManager;
	private @Autowired IHashUtil iHashUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired KeyServerUtil keyServerUtil;
	private @Autowired ITGTService iTGTService;
	
	public enum TicketAttributes{
		TGT_EXPIRY_TIME, TICKET, SESSION_KEY;
	}

	
	@Override
	public String fetchPassworkFromDirectoryForUsername(String username) throws InvalidAttributeValueException{
		
		log.debug("Entering fetchPassworkFromDirectoryForUsername");
		
		if (username == null || username.isEmpty()){
			log.error("Invalid input to fetchPassworkFromDirectoryForUsername, username can not be null or empty");
			throw new InvalidAttributeValueException();
		}
		
		String password  = null;
		try {
			password = activeDirectory.findPasswordForApp(username);
		} catch (NamingException | IOException e) {
			log.error("Error fetching password from Active Directory for Application username. Detail exception attached below:");
			e.printStackTrace();
		}
		
		log.debug("Returning from fetchPasswordFromDirectoryForUsername");
		
		return password;
	}
	
	@Override
	public Map<TicketAttributes, String> createResponseAttrbiutes(String username, Date tgtExpiryDateTime, String sessionKey) throws InvalidAttributeValueException{
		
		log.debug("Entering the createResponseAttributes");
		
		if (username == null || username.isEmpty()){
			log.error("Invalid input to createResponeAttributes, username can not be null or empty");
			throw new InvalidAttributeValueException();
		}
		String tgtExpiryDateTimeStr = iDateUtil.generateStringFromDate(tgtExpiryDateTime);
		
		//Creating the TGT by encrypting Username, SessionKey and TGTExpiryTime in a single String
		String ticket;
		try {
			ticket = iEncryptionUtil.encrypt(keyServerUtil.getKeyFromKeyStore(null, SecretKeyType.KDC_MASTER_KEY), new StringBuilder(username).append(",").append(sessionKey).append(",").append(tgtExpiryDateTimeStr).toString())[0];
		} catch (InvalidAttributeValueException | NoSuchAlgorithmException
				| UnrecoverableEntryException | KeyStoreException
				| CertificateException | NamingException | IOException e) {
			log.error("Unable to encrypt the response attributes. Appending the detailed stacktrace");
			e.printStackTrace();
			return null;
		}
		
		Map<TicketAttributes, String> responseAttributes = new HashMap<TicketAttributes, String>();
		
		responseAttributes.put(TicketAttributes.TICKET, ticket);
		responseAttributes.put(TicketAttributes.TGT_EXPIRY_TIME, tgtExpiryDateTimeStr);
		responseAttributes.put(TicketAttributes.SESSION_KEY, sessionKey);
		
		log.debug("Returning from createResponseAttributes method");
		
		return responseAttributes;
	}

	@Override
	public void createTGT(String username, String sessionKey, Date tgtExpiryDate) throws InvalidAttributeValueException{
	
		log.debug("Entring the createTGT method");
		
		if (username == null || username.isEmpty() || sessionKey == null || sessionKey.isEmpty() || tgtExpiryDate == null){
			log.error("Invalid input parameters to createTGT");
			throw new InvalidAttributeValueException("Invalid input parameter to createTGT");
		}
		
		TGT tgt = new TGT();
		tgt.setUsername(username);
		tgt.setSessionKey(sessionKey);
		tgt.setExpiresOn(tgtExpiryDate);
		
		iTGTService.saveTGT(tgt);
		
	}

	@Override
	public AuthenticationResponse createAuthenticationResponse(String encUsername, String encSessionKey, String encTGTPacket) throws InvalidAttributeValueException{
		
		log.debug("Entering createAuthenticationResponse");
		if (encUsername == null || encUsername.isEmpty() || encSessionKey == null || encSessionKey.isEmpty() || encTGTPacket == null || encTGTPacket.isEmpty()){
			log.error("Invalid Input parameter to createAuthenticationResponse");
			throw new InvalidAttributeValueException("Invalid input parameter to createAuthenticationResponse");
		}
		AuthenticationResponse response = new AuthenticationResponse();
		response.setEncLoginName(encUsername);
		response.setEncSessionKey(encSessionKey);
		response.setEncTgtPacket(encTGTPacket);
		
		log.debug("Returning from createAuthenticationResponse");
		
		return response;
	}

	@Override
	public SecretKey generatePasswordSymmetricKey(String loginAppName, String appPassword) throws NoSuchAlgorithmException, InvalidAttributeValueException{
		
		log.debug("Entering the generatePasswordSymmetricKey");
		
		if (loginAppName == null || loginAppName.isEmpty() || appPassword == null || appPassword.isEmpty()){
			log.error("Invalid Input parameter to generatePasswordSymmetricKey");
			throw new InvalidAttributeValueException("Invalid input paramter to generatePasswordSymmetricKey");
		}

		SecretKey passwordSymmetricKey = iEncryptionUtil.generateSecretKey(appPassword);
		
		log.debug("Returning from generatePasswordSymmetricKey");
		
		return passwordSymmetricKey;
	}
	
	@Override
	public boolean checkIfTGTIsValid(TGT tgt) {
		
		log.debug("Entering checkIfTGTIsValid");
		
		if (tgt == null){
			return false;
		}
		
		//If exprity time is before the current date then, the ticket is expired
		if (tgt.getExpiresOn().before(new Date())){
			iTGTService.deactiveTGT(tgt);
			return false;
		}
		
		return true;
	}
}
