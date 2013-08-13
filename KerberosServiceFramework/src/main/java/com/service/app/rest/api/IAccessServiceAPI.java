/**
 * 
 */
package com.service.app.rest.api;

import java.util.Map;

import com.service.app.rest.representation.AccessServiceRequest;
import com.service.app.rest.representation.AccessServiceResponse;
import com.service.rest.exception.common.AuthenticatorValidationException;
import com.service.rest.exception.common.UnauthenticatedAppException;
import com.service.rest.exception.common.UnauthenticatedUserException;

/**
 * @author raunak
 *
 */
public interface IAccessServiceAPI {
	
	/**
	 * @param request
	 * @return Map<String, String> or null incase of invalid input parameter
	 * @throws UnauthenticatedAppException 
	 * @throws UnauthenticatedUserException 
	 * @throws AuthenticatorValidationException 
	 */
	Map<String, String> processAccessServiceRequest(AccessServiceRequest request)
			throws UnauthenticatedAppException, UnauthenticatedUserException, AuthenticatorValidationException;

	/**
	 * @param request
	 * @param responseData
	 * @return AccessServiceResponse or null in case invalid input parameter
	 */
	AccessServiceResponse generateAccessServiceResponse(
			AccessServiceRequest request, Map<String, String> responseData);

	
}
