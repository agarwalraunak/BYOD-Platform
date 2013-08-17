/**
 * 
 */
package com.login.kerberos.rest.api;

import java.io.IOException;
import java.util.Map;

import javax.crypto.SecretKey;

import com.login.exception.ResponseDecryptionException;
import com.login.exception.RestClientException;
import com.login.kerberos.rest.api.KerberosAuthenticationAPIImpl.AuthenticationResponseAttributes;

/**
 * @author raunak
 *
 */
public interface IKerberosAuthenticationAPI {

	/**
	 * @param url
	 * @param loginName
	 * @param password
	 * @param isApplication
	 * @return
	 * @throws IOException 
	 * @throws ResponseDecryptionException 
	 * @throws RestClientException 
	 */
	Map<AuthenticationResponseAttributes, String> authenticate(String url,	String loginName, String password) throws IOException, RestClientException, ResponseDecryptionException, com.login.exception.RestClientException;

	/**
	 * @param loginAppName
	 * @param appPassword
	 * @return
	 */
	SecretKey generatePasswordSymmetricKey(String loginAppName,
			String appPassword) ;
}
