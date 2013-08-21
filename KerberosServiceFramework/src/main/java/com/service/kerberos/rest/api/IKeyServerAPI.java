/**
 * 
 */
package com.service.kerberos.rest.api;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import com.service.kerberos.rest.api.KerberosAuthenticationAPIImpl.SecretKeyType;
import com.service.kerberos.rest.representation.KeyServerResponse;

/**
 * This interface provides the methods required to get <code>SecretKey</code> from Key Server
 * 
 * @author raunak
 *
 */
public interface IKeyServerAPI {
	
	/**
	 * This method process the Response from the Key Server and returns the Response
	 * @param <code>KeyServerResponse</code> response
	 * @param <code>String</code> requestAuthenticator
	 * @param <code>SecretKey</code> serviceSessionKey
	 * @return
	 * <code>KeyServerResponse</code>
	 */
	KeyServerResponse processKeyServerResponse(KeyServerResponse response,
			Date requestAuthenticator, SecretKey serviceSessionKey) ;

	/**
	 * Retrieves the key from response
	 * @param responseData
	 * @param serviceSessionKey
	 * @param keyType
	 * @return
	 */
	SecretKey getKeyFromResponseData(Map<String, String> responseData,
			SecretKey serviceSessionKey, SecretKeyType keyType)
			;

	
}
