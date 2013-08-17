/**
 * 
 */
package com.service.kerberos.rest.client;

import java.io.IOException;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.config.KerberosURLConfig;
import com.service.exception.RestClientException;
import com.service.kerberos.rest.api.IKeyServerAPI;
import com.service.kerberos.rest.api.KerberosAuthenticationAPIImpl.SecretKeyType;
import com.service.kerberos.rest.representation.KeyServerRequest;
import com.service.kerberos.rest.representation.KeyServerResponse;
import com.service.model.kerberos.ServiceTicket;
import com.service.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.service.util.connectionmanager.ConnectionManagerImpl.RequestMethod;
import com.service.util.connectionmanager.IConnectionManager;
import com.service.util.dateutil.IDateUtil;
import com.service.util.encryption.IEncryptionUtil;
import com.service.util.hashing.IHashUtil;

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
	public SecretKey getKeyFromKeyServer(ServiceTicket serviceTicket, SecretKeyType keyType) {
		
		log.debug("Entering getKeyFromKeyServer method");
		
		if (serviceTicket == null || keyType == null){
			log.error("Invalid input parameter to getKeyFromKeyServer");
			return null;
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
		} catch (IOException|RestClientException e) {
			e.printStackTrace();
			return null;
		}
		
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
		String encServiceKey = responseData.get(keyType.getValue());
		SecretKey serviceKey = iEncryptionUtil.generateSecretKeyFromBytes(iHashUtil.stringToByte(encServiceKey));
		
		return serviceKey;
	}
	

}
