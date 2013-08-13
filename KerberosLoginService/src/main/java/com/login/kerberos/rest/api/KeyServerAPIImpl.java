/**
 * 
 */
package com.login.kerberos.rest.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.app.model.AppSessionDirectory;
import com.login.kerberos.rest.api.KerberosAuthenticationAPIImpl.SecretKeyType;
import com.login.kerberos.rest.representation.KeyServerResponse;
import com.login.rest.exceptions.ServiceUnavailableException;
import com.login.util.dateutil.IDateUtil;
import com.login.util.encryption.IEncryptionUtil;
import com.login.util.hashing.IHashUtil;

/**
 * @author raunak
 *
 */
@Component
public class KeyServerAPIImpl implements IKeyServerAPI{
	
	private static Logger log = Logger.getLogger(KeyServerAPIImpl.class);

	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IHashUtil iHashUtil;
	private @Autowired AppSessionDirectory appSessionDirectory;
	
	
	@Override
	public KeyServerResponse processKeyServerResponse(KeyServerResponse response, Date requestAuthenticator, SecretKey serviceSessionKey) throws InvalidAttributeValueException{
		
		log.debug("Entering processResponse method");
		
		if (response == null || requestAuthenticator == null || serviceSessionKey == null){
			log.error("Invalid input parameter to processResponse");
			throw new InvalidAttributeValueException("Invalid input parameter to processResponse");
		}
		
		String responseAuthenticatorStr = iEncryptionUtil.decrypt(serviceSessionKey, response.getEncResponseAuthenticator())[0];
		Date responseAuthenticator = iDateUtil.generateDateFromString(responseAuthenticatorStr);
		
		if (!iDateUtil.validateAuthenticator(responseAuthenticator, requestAuthenticator)){
			return null;
		}
		
		Map<String, String> encResponseData = response.getResponseData();
		if (encResponseData == null){
			return null;
		}
		
		Map<String, String> responseData = new HashMap<String, String>();
		
		Iterator<String> iterator = encResponseData.keySet().iterator();
		String key = null;
		while(iterator.hasNext()){
			key = iterator.next();
			responseData.put(key, iEncryptionUtil.decrypt(serviceSessionKey, encResponseData.get(key))[0]);
		}
		
		response.setResponseData(responseData);
		
		return response;
		
	}
	
	@Override 
	public SecretKey getKeyFromResponseData(Map<String, String> responseData, SecretKey serviceSessionKey, SecretKeyType keyType) throws InvalidAttributeValueException, ServiceUnavailableException{
		
		log.debug("Entering getKeyFromResponseData method");
		
		if (responseData == null || serviceSessionKey == null || keyType == null){
			log.error("Invalid parameter provided in getKeyFromResponseData");
			throw new InvalidAttributeValueException("Invalid parameter provided in getKeyFromResponseData");
		}
		
		String serviceKeyStr = responseData.get(keyType.getValue());
		if (serviceKeyStr == null){
			log.error("Unable to get the key from key server");
			throw new ServiceUnavailableException("Error processing the request. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		String decServiceKeyStr = null;
		try {
			decServiceKeyStr = iEncryptionUtil.decrypt(serviceSessionKey, serviceKeyStr)[0];
		} catch (InvalidAttributeValueException e) {
			log.error("Error processing the request. Detailed Exception is attached below: \n"+e.getMessage());
			e.printStackTrace();
			throw new ServiceUnavailableException("Error processing the request. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		if(decServiceKeyStr == null){
			log.error("Error processing the request");
			throw new ServiceUnavailableException("Error processing the request. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		SecretKey serviceKey = iEncryptionUtil.generateSecretKeyFromBytes(iHashUtil.stringToByte(decServiceKeyStr));
		
		return serviceKey;
	}
}