/**
 * 
 */
package com.kerberos.service.rest.client;

import java.io.IOException;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kerberos.rest.representation.kerberos.KeyServerRequest;
import com.kerberos.rest.representation.kerberos.KeyServerResponse;
import com.kerberos.service.models.ServiceTicket;
import com.kerberos.service.rest.api.app.IAccessServiceAPI;
import com.kerberos.service.rest.api.kerberos.KerberosAuthenticationAPIImpl.SecretKeyType;
import com.kerberos.service.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.kerberos.service.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.kerberos.service.util.connectionmanager.IConnectionManager;
import com.kerberos.service.util.dateutil.IDateUtil;
import com.kerberos.service.util.encryption.IEncryptionUtil;
import com.kerberos.service.util.hashing.IHashUtil;

/**
 * @author raunak
 *
 */
public class KerberosKeyServerClient {
	
	private static Logger log = Logger.getLogger(KerberosKeyServerClient.class);
	
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IConnectionManager iConnectionManager;
	private @Autowired IAccessServiceAPI iAccessServiceAPI;
	private @Autowired IHashUtil iHashUtil;
	private static final String url = "http://localhost:8080/kerberos/apple/keyserver/key";
	
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
			response = (KeyServerResponse) iConnectionManager.generateRequest(url, RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, KeyServerResponse.class, iConnectionManager.generateJSONStringForObject(request));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		if (response == null){
			return null;
		}
		
		response =  iAccessServiceAPI.processKeyServerResponse(response, iDateUtil.generateDateFromString(requestAuthenticator), serviceSessionKey);
		
		Map<String, String> responseData = response.getResponseData();
		if (responseData == null){
			return null;
		}
		String encServiceKey = responseData.get(keyType.getValue());
		SecretKey serviceKey = iEncryptionUtil.generateSecretKeyFromBytes(iHashUtil.stringToByte(encServiceKey));
		
		return serviceKey;
	}
	

}
