/**
 * 
 */
package com.service.service.login.rest.client;

import com.service.exception.common.InternalSystemException;
import com.service.model.service.ServiceSession;

/**
 * @author raunak
 *
 */
public interface ILoginServerValidateUserAuthenticationClient {

	/**
	 * @param appLoginServerSession
	 * @param kerberosServiceSessionID
	 * @param userLoginServiceSession
	 * @return
	 * @throws InternalSystemException
	 */
	boolean validateUserAuthenticationAgainstLoginServer(
			ServiceSession appLoginServerSession, String kerberosServiceSessionID, String userLoginServiceSession)
			throws InternalSystemException;

}
