/**
 * 
 */
package com.login.kerberos.rest.client;

import java.io.IOException;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.config.KerberosURLConfig;
import com.login.kerberos.model.ServiceTicket;
import com.login.kerberos.rest.api.IKeyServerAPI;
import com.login.kerberos.rest.representation.KeyServerRequest;
import com.login.kerberos.rest.representation.KeyServerResponse;
import com.login.util.ActiveDirectory.ActiveDirectoryImpl.SecretKeyType;
import com.login.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.login.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.login.util.connectionmanager.IConnectionManager;
import com.login.util.dateutil.IDateUtil;
import com.login.util.encryption.IEncryptionUtil;
import com.login.util.hashing.IHashUtil;

/**
 * @author raunak
 *
 */
@Component
public class KerberosKeyServerClient {
	
	private static Logger log = Logger.getLogger(KerberosKeyServerClient.class);
	
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IConnectionManager iConnectionManager;
	private @Autowired IKeyServerAPI iKeyServerAPI;
	private @Autowired IHashUtil iHashUtil;
	private @Autowired KerberosURLConfig kerberosURLConfig;
	
	public SecretKey getKeyFromKeyServer(ServiceTicket serviceTicket, SecretKeyType keyType) throws InvalidAttributeValueException{
		
		log.debug("Entering getKeyFromKeyServer method");
		
		if (serviceTicket == null || keyType == null){
			log.error("Invalid input parameter to getKeyFromKeyServer");
			throw new InvalidAttributeValueException("Invalid input parameter to getKeyFromKeyServer");
		}
		
		String requestAuthenticator = iDateUtil.createAuthenticator();
		SecretKey serviceSessionKey = iEncryptionUtil.generateSecretKey(serviceTicket.getServiceSessionID());
				
		String[] encryptedData = iEncryptionUtil.encrypt(serviceSessionKey, requestAuthenticator, keyType.getValue());
		String encAuthenticator = encryptedData[0];
		String encKeyType = encryptedData[1];
		
		KeyServerRequest request = new KeyServerRequest();
		request.setEncServiceTicket(serviceTicket.getEncServiceTicket());
		request.setEncAuthenticator(encAuthenticator);
		request.setKeyType(encKeyType);
		
		KeyServerResponse response;
		try {
			response = (KeyServerResponse) iConnectionManager.generateRequest(kerberosURLConfig.getKERBEROS_KEY_SERVER_KEY_REQUEST_URL(), RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, KeyServerResponse.class, iConnectionManager.generateJSONStringForObject(request));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		if (response == null){
			return null;
		}
		
		response =  iKeyServerAPI.processKeyServerResponse(response, iDateUtil.generateDateFromString(requestAuthenticator), serviceSessionKey);
		
		Map<String, String> responseData = response.getResponseData();
		if (responseData == null){
			return null;
		}
		String encServiceKey = responseData.get(keyType.getValue());
		SecretKey serviceKey = iEncryptionUtil.generateSecretKeyFromBytes(iHashUtil.stringToByte(encServiceKey));
		
		return serviceKey;
	}
	

}
