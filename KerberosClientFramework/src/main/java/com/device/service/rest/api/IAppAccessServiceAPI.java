/**
 * 
 */
package com.device.service.rest.api;

import java.util.Date;
import java.util.Map;

import com.device.exception.InvalidResponseAuthenticatorException;
import com.device.service.model.AppSession;
import com.device.service.rest.representation.AppAccessServiceRequest;
import com.device.service.rest.representation.AppAccessServiceResponse;

/**
 * @author raunak
 *
 */
public interface IAppAccessServiceAPI {


	/**
	 * @param response
	 * @param requestAuthenticator
	 * @param appSession
	 * @return
	 * @throws InvalidResponseAuthenticatorException
	 */
	Map<String, String> processAppAccessServiceResponse(
			AppAccessServiceResponse response, Date requestAuthenticator,
			AppSession appSession) throws InvalidResponseAuthenticatorException;

	/**
	 * @param serviceSessionID
	 * @param appSession
	 * @param requestAuthenticator
	 * @param requestData
	 * @return
	 */
	AppAccessServiceRequest generateAppAccessServiceRequest(
			String serviceSessionID, AppSession appSession,
			Date requestAuthenticator, Map<String, String> requestData);


}
