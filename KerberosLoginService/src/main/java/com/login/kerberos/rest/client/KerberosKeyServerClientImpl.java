/**
 * 
 */
package com.login.kerberos.rest.client;

import java.io.IOException;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.config.KerberosURLConfig;
import com.login.exception.RestClientException;
import com.login.kerberos.rest.api.IKeyServerAPI;
import com.login.kerberos.rest.api.KerberosAuthenticationAPIImpl.SecretKeyType;
import com.login.kerberos.rest.representation.KeyServerRequest;
import com.login.kerberos.rest.representation.KeyServerResponse;
import com.login.model.kerberos.ServiceTicket;
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
public class KerberosKeyServerClientImpl implements IKerberosKeyServerClient{
	
	private static Logger log = Logger.getLogger(KerberosKeyServerClientImpl.class);
	
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IConnectionManager iConnectionManager;
	private @Autowired IHashUtil iHashUtil;
	
	private @Autowired IKeyServerAPI iKeyServerAPI;
	
	private @Autowired KerberosURLConfig kerberosURLConfig;
	
	@Override
	public SecretKey getKeyFromKeyServer(ServiceTicket serviceTicket, SecretKeyType keyType) throws RestClientException, IOException {
		
		log.debug("Entering getKeyFromKeyServer method");
		
		if (serviceTicket == null || keyType == null){
			log.error("Invalid input parameter to getKeyFromKeyServer");
			throw new IllegalArgumentException("Invalid input parameter to getKeyFromKeyServer");
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
		
		KeyServerResponse response = (KeyServerResponse) iConnectionManager.generateRequest(kerberosURLConfig.getKERBEROS_KEY_SERVER_KEY_REQUEST_URL(), RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, KeyServerResponse.class, iConnectionManager.generateJSONStringForObject(request));
		
		if (response == null){
			return null;
		}
		
		response =  iKeyServerAPI.processKeyServerResponse(response, iDateUtil.generateDateFromString(requestAuthenticator), serviceSessionKey);
		if (response == null){
			return null;
		}
		
		Map<String, String> responseData = response.getResponseData();
		if (responseData == null){
			return null;
		}
		String serviceKeyStr = responseData.get(keyType.getValue());
		SecretKey serviceKey = iEncryptionUtil.generateSecretKeyFromBytes(iHashUtil.stringToByte(serviceKeyStr));
		
		return serviceKey;
	}
	

}
