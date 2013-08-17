/**
 * 
 */
package com.device.kerberos.rest.api;

import java.io.IOException;
import java.util.Map;

import javax.crypto.SecretKey;

import com.device.exception.ResponseDecryptionException;
import com.device.exception.RestClientException;
import com.device.kerberos.rest.api.KerberosAuthenticationAPIImpl.AuthenticationResponseAttributes;

/**
 * @author raunak
 *
 */
public interface IKerberosAuthenticationAPI {

	/**
	 * @param url
	 * @param loginName
	 * @param password
	 * @return
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws ResponseDecryptionException 
	 */
	Map<AuthenticationResponseAttributes, String> authenticate(String url,	String loginName, String password) throws IOException, RestClientException, ResponseDecryptionException;

	/**
	 * @param loginAppName
	 * @param appPassword
	 * @return
	 */
	SecretKey generatePasswordSymmetricKey(String loginAppName,
			String appPassword) ;
}
