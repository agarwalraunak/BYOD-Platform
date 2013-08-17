/**
 * 
 */
package com.service.app.rest.api;

import java.util.Map;

import com.service.app.rest.representation.UserAccessServiceRequest;
import com.service.app.rest.representation.UserAccessServiceResponse;
import com.service.exception.common.AuthenticatorValidationException;
import com.service.exception.common.UnauthenticatedAppException;
import com.service.exception.common.UnauthenticatedUserException;

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
	Map<String, String> processAccessServiceRequest(UserAccessServiceRequest request)
			throws UnauthenticatedAppException, UnauthenticatedUserException, AuthenticatorValidationException;

	/**
	 * @param request
	 * @param responseData
	 * @return AccessServiceResponse or null in case invalid input parameter
	 */
	UserAccessServiceResponse generateAccessServiceResponse(
			UserAccessServiceRequest request, Map<String, String> responseData);

	
}
