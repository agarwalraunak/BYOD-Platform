/**
 * 
 */
package com.service.kerberos.rest.api;

import java.util.Map;

import javax.crypto.SecretKey;

import com.service.kerberos.rest.api.KerberosAuthenticationAPIImpl.AuthenticationResponseAttributes;
import com.service.rest.exception.common.InternalSystemException;

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
	 * @throws InternalSystemException 
	 */
	Map<AuthenticationResponseAttributes, String> authenticate(String url,	String loginName, String password, boolean isApplication) throws InternalSystemException;

	/**
	 * @param loginAppName
	 * @param appPassword
	 * @return
	 * @throws InternalSystemException 
	 */
	SecretKey generatePasswordSymmetricKey(String loginAppName,
			String appPassword) throws InternalSystemException;
}
