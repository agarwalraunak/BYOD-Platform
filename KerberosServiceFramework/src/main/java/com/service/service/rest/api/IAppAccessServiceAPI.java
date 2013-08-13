/**
 * 
 */
package com.service.service.rest.api;

import java.util.Date;
import java.util.Map;

import com.service.model.kerberos.KerberosAppSession;
import com.service.model.service.ServiceSession;
import com.service.rest.exception.common.AuthenticatorValidationException;
import com.service.service.rest.representation.AppAccessServiceRequest;
import com.service.service.rest.representation.AppAccessServiceResponse;

/**
 * @author raunak
 *
 */
public interface IAppAccessServiceAPI {

	/**
	 * @param kerberosAppSession
	 * @param serviceSessionID
	 * @param serviceSession
	 * @param requestAuthenticator
	 * @param requestData
	 * @return
	 */
	AppAccessServiceRequest generateAppAccessServiceRequest(
			KerberosAppSession kerberosAppSession, String serviceSessionID,
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
