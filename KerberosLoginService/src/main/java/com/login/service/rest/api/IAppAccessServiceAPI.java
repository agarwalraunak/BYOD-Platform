package com.login.service.rest.api;

import java.util.Map;

import javax.management.InvalidAttributeValueException;

import com.login.service.rest.representation.AppAccessServiceRequest;
import com.login.service.rest.representation.AppAccessServiceResponse;

public interface IAppAccessServiceAPI {

	/**
	 * This method handles the channel security and request authentication in the scenario when only Applications (note, no user involved) would want to have transactions 
	 * between them. The template of the Request is com.login.service.rest.representation.AppAccessServiceRequest. The requesting application can send data using the Map provided 
	 * in the request template. 
	 * @param request
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	Map<String, String> processAppAccessServiceRequest(
			AppAccessServiceRequest request)
			throws InvalidAttributeValueException;

	/**
	 * @param request
	 * @param responseData
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	AppAccessServiceResponse generateAppAccessServiceResponse(
			AppAccessServiceRequest request, Map<String, String> responseData)
			throws InvalidAttributeValueException;

}
