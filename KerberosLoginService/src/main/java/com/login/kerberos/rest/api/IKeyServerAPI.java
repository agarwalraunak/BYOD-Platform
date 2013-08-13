/**
 * 
 */
package com.login.kerberos.rest.api;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import com.login.kerberos.rest.api.KerberosAuthenticationAPIImpl.SecretKeyType;
import com.login.kerberos.rest.representation.KeyServerResponse;
import com.login.rest.exceptions.ServiceUnavailableException;

/**
 * @author raunak
 *
 */
public interface IKeyServerAPI {
	
	/**
	 * @param response
	 * @param requestAuthenticator
	 * @param serviceSessionKey
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	KeyServerResponse processKeyServerResponse(KeyServerResponse response,
			Date requestAuthenticator, SecretKey serviceSessionKey) throws InvalidAttributeValueException;

	/**
	 * @param responseData
	 * @param serviceSessionKey
	 * @param keyType
	 * @return
	 * @throws InvalidAttributeValueException
	 * @throws ServiceUnavailableException
	 */
	SecretKey getKeyFromResponseData(Map<String, String> responseData,
			SecretKey serviceSessionKey, SecretKeyType keyType)
			throws InvalidAttributeValueException, ServiceUnavailableException;

	
}
