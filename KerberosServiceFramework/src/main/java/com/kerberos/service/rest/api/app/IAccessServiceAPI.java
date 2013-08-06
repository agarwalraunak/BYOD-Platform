/**
 * 
 */
package com.kerberos.service.rest.api.app;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import com.kerberos.rest.representation.device.AccessServiceRequest;
import com.kerberos.rest.representation.device.AccessServiceResponse;
import com.kerberos.rest.representation.kerberos.KeyServerResponse;
import com.kerberos.service.rest.api.kerberos.KerberosAuthenticationAPIImpl.SecretKeyType;
import com.kerberos.service.rest.exceptions.ServiceUnavailableException;

/**
 * @author raunak
 *
 */
public interface IAccessServiceAPI {
	
	/**
	 * @param response
	 * @param requestAuthenticator
	 * @param serviceSessionKey
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	KeyServerResponse processKeyServerResponse(KeyServerResponse response,
			Date requestAuthenticator, SecretKey serviceSessionKey) throws InvalidAttributeValueException;

	/**
	 * @param responseData
	 * @param serviceSessionKey
	 * @param keyType
	 * @return
	 * @throws InvalidAttributeValueException
	 * @throws ServiceUnavailableException
	 */
	SecretKey getKeyFromResponseData(Map<String, String> responseData,
			SecretKey serviceSessionKey, SecretKeyType keyType)
			throws InvalidAttributeValueException, ServiceUnavailableException;

	/**
	 * @param request
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	Map<String, String> processAccessServiceRequest(AccessServiceRequest request)
			throws InvalidAttributeValueException;

	/**
	 * @param request
	 * @param responseData
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	AccessServiceResponse generateAccessServiceResponse(
			AccessServiceRequest request, Map<String, String> responseData)
			throws InvalidAttributeValueException;

	
}
