/**
 * 
 */
package com.service.service.login.rest.client;

import com.service.model.service.ServiceSession;
import com.service.rest.exception.common.InternalSystemException;

/**
 * @author raunak
 *
 */
public interface ILoginServerValidateUserAuthenticationClient {

	/**
	 * @param appLoginServerSession
	 * @param userLoginServiceSession
	 * @return boolean true if user is authenticated else false or if the input parameters to the method are not valid
	 * @throws InternalSystemException
	 */
	boolean validateUserAuthenticationAgainstLoginServer(
			ServiceSession appLoginServerSession, String userLoginServiceSession)
			throws InternalSystemException;

}
