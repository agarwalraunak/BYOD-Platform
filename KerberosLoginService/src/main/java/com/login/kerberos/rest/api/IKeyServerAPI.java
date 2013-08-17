/**
 * 
 */
package com.login.kerberos.rest.api;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import com.login.kerberos.rest.api.KerberosAuthenticationAPIImpl.SecretKeyType;
import com.login.kerberos.rest.representation.KeyServerResponse;

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
	 */
	KeyServerResponse processKeyServerResponse(KeyServerResponse response,
			Date requestAuthenticator, SecretKey serviceSessionKey) ;

	/**
	 * @param responseData
	 * @param serviceSessionKey
	 * @param keyType
	 * @return
	 */
	SecretKey getKeyFromResponseData(Map<String, String> responseData,
			SecretKey serviceSessionKey, SecretKeyType keyType)
			;

	
}
