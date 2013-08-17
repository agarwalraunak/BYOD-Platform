/**
 * 
 */
package com.service.kerberos.rest.api;

import java.io.IOException;
import java.util.Map;

import javax.crypto.SecretKey;

import com.service.exception.ResponseDecryptionException;
import com.service.exception.RestClientException;
import com.service.kerberos.rest.api.KerberosAuthenticationAPIImpl.AuthenticationResponseAttributes;

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
