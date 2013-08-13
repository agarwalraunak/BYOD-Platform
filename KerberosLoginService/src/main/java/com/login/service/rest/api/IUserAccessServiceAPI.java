/**
 * 
 */
package com.login.service.rest.api;

import java.util.Map;

import javax.management.InvalidAttributeValueException;

import com.login.service.rest.representation.AccessServiceRequest;
import com.login.service.rest.representation.AccessServiceResponse;

/**
 * @author raunak
 *
 */
public interface IUserAccessServiceAPI {

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
