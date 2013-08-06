/**
 * 
 */
package com.kerberos.rest.api;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import com.kerberos.rest.representation.AccessServiceResponse;
import com.kerberos.rest.representation.KeyServerRequest;

/**
 * @author raunak
 *
 */
public interface IAccessServiceAPI {

	/**
	 * @param request
	 * @param serviceKey
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	public AccessServiceResponse processKeyServerRequest(KeyServerRequest request, SecretKey serviceKey) throws InvalidAttributeValueException;
}
