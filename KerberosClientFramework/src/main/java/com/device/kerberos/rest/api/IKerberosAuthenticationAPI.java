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
 * It provides the methods necessary for performing <strong>Kerberos Authentication</strong>
 * 
 * @author raunak
 *
 */
public interface IKerberosAuthenticationAPI {

	/**
	 * Performs <strong>Kerberos App Authentication</strong>  
	 * @param <code>String</code> url of the Kerberos Server to be called for Authentication
	 * @param <code>String</code> App Login Name
	 * @param <code>String</code> App Password
	 * @return
	 * <code>Map<AuthenticationResponseAttributes, String></code> 
	 * @throws IOException
	 * In case there are some errors encountered while retrieving information
	 * @throws RestClientException
	 * If the status of the response is not <strong>200</strong>. The server side error message and error 
	 * response code can be accessed using <code>getMessage</code> and <code>getErrorCode</code> methods respectively
	 * @throws ResponseDecryptionException
	 * If the Application was unable to decrypt the Response sent by the server
	 *  
	 */
	Map<AuthenticationResponseAttributes, String> authenticate(String url,	String loginName, String password) throws IOException, RestClientException, ResponseDecryptionException;

	/**
	 * Returns a SecretKey generated using App Login Name and Password
	 * @param <code>String</code> App Login Name
	 * @param <code>String</code> App Password
	 * @return
	 * <code>SecretKey</code> generated using Login Name and Password
	 */
	SecretKey generatePasswordSymmetricKey(String loginAppName,
			String appPassword) ;
}
