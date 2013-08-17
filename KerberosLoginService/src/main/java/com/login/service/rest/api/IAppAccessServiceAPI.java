package com.login.service.rest.api;

import java.util.Map;

import com.login.exception.common.AuthenticatorValidationException;
import com.login.exception.common.UnauthenticatedAppException;
import com.login.service.rest.representation.AppAccessServiceRequest;
import com.login.service.rest.representation.AppAccessServiceResponse;

public interface IAppAccessServiceAPI {

	/**
	 * This method handles the channel security and request authentication in the scenario when only Applications (note, no user involved) would want to have transactions 
	 * between them. The template of the Request is com.login.service.rest.representation.AppAccessServiceRequest. The requesting application can send data using the Map provided 
	 * in the request template. 
	 * @param request
	 * @return
	 * @throws AuthenticatorValidationException 
	 * @throws UnauthenticatedAppException 
	 */
	Map<String, String> processAppAccessServiceRequest(
			AppAccessServiceRequest request)
			throws AuthenticatorValidationException, UnauthenticatedAppException;

	/**
	 * @param request
	 * @param responseData
	 * @return
	 */
	AppAccessServiceResponse generateAppAccessServiceResponse(
			AppAccessServiceRequest request, Map<String, String> responseData);

}
