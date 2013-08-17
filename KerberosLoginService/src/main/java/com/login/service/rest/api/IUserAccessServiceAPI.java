/**
 * 
 */
package com.login.service.rest.api;

import java.util.Map;

import com.login.exception.common.AuthenticatorValidationException;
import com.login.exception.common.UnauthenticatedAppException;
import com.login.exception.common.UnauthenticatedUserException;
import com.login.service.rest.representation.UserAccessServiceRequest;
import com.login.service.rest.representation.UserAccessServiceResponse;

/**
 * @author raunak
 *
 */
public interface IUserAccessServiceAPI {

	/**
	 * @param request
	 * @return
	 * @throws AuthenticatorValidationException 
	 * @throws UnauthenticatedAppException 
	 * @throws UnauthenticatedUserException 
	 */
	Map<String, String> processAccessServiceRequest(UserAccessServiceRequest request)
			throws UnauthenticatedAppException, AuthenticatorValidationException, UnauthenticatedUserException;

	/**
	 * @param request
	 * @param responseData
	 * @return
	 * @throws UnauthenticatedAppException 
	 * @throws UnauthenticatedUserException 
	 */
	UserAccessServiceResponse generateAccessServiceResponse(
			UserAccessServiceRequest request, Map<String, String> responseData)
			throws UnauthenticatedAppException, UnauthenticatedUserException;

	
}
