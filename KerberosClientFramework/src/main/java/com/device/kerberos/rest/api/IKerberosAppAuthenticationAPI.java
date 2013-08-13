/**
 * 
 */
package com.device.kerberos.rest.api;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import com.device.kerberos.rest.api.KerberosAppAuthenticationAPIImpl.AuthenticationResponseAttributes;

/**
 * @author raunak
 *
 */
public interface IKerberosAppAuthenticationAPI {

	/**
	 * @param url
	 * @param loginName
	 * @param password
	 * @param isApplication
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAttributeValueException
	 */
	Map<AuthenticationResponseAttributes, String> authenticate(String url,	String loginName, String password, boolean isApplication)	throws IOException, NoSuchAlgorithmException, InvalidAttributeValueException;

	SecretKey generatePasswordSymmetricKey(String loginAppName,
			String appPassword) throws NoSuchAlgorithmException,
			InvalidAttributeValueException;
}
