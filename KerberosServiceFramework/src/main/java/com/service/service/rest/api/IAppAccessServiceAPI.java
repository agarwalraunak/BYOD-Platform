/**
 * 
 */
package com.service.service.rest.api;

import java.util.Date;
import java.util.Map;

import com.service.exception.common.AuthenticatorValidationException;
import com.service.model.service.ServiceSession;
import com.service.service.rest.representation.AppAccessServiceRequest;
import com.service.service.rest.representation.AppAccessServiceResponse;

/**
 * @author raunak
 *
 */
public interface IAppAccessServiceAPI {

	/**
	 * @param serviceSessionID
	 * @param serviceSession
	 * @param requestAuthenticator
	 * @param requestData
	 * @return
	 */
	AppAccessServiceRequest generateAppAccessServiceRequest( String serviceSessionID,
			ServiceSession serviceSession, Date requestAuthenticator,
			Map<String, String> requestData);

	/**
	 * @param response
	 * @param requestAuthenticator
	 * @param serviceSession
	 * @return Map<String, String> or null if input parameters are invalid
	 * @throws AuthenticatorValidationException 
	 */
	Map<String, String> processAppAccessServiceResponse(
			AppAccessServiceResponse response, Date requestAuthenticator,
			ServiceSession serviceSession) throws AuthenticatorValidationException;


}
