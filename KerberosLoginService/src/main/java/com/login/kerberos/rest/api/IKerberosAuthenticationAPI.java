/**
 * 
 */
package com.login.kerberos.rest.api;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

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
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAttributeValueException
	 */
	Map<AuthenticationResponseAttributes, String> authenticate(String url,	String loginName, String password, boolean isApplication)	throws IOException, NoSuchAlgorithmException, InvalidAttributeValueException;

	/**
	 * @param loginAppName
	 * @param appPassword
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAttributeValueException
	 */
	SecretKey generatePasswordSymmetricKey(String loginAppName,
			String appPassword) throws NoSuchAlgorithmException,
			InvalidAttributeValueException;
}
