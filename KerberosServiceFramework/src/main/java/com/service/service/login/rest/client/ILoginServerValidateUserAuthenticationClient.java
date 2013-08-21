/**
 * 
 */
package com.service.service.login.rest.client;

import com.service.exception.common.InternalSystemException;
import com.service.model.service.ServiceSession;

/**
 * This interface provides the functionality to validate <strong>User Authentication</strong> 
 * against <strong>Login Server</strong>
 * 
 * @author raunak
 *
 */
public interface ILoginServerValidateUserAuthenticationClient {

	/**
	 * Returns true if user is authenticated else false
	 * @param <code>ServiceSession</code> appLoginServerSession
	 * @param <code>String</code> kerberosServiceSessionID
	 * @param <code>String</code> userLoginServiceSession
	 * @return
	 * <code>boolean</code> true if User is Authenticated else false
	 * @throws <code>InternalSystemException</code>
	 * <p>In case it encounters any problem while validating User Authentication</p>
	 */
	boolean validateUserAuthenticationAgainstLoginServer(
			ServiceSession appLoginServerSession, String kerberosServiceSessionID, String userLoginServiceSession)
			throws InternalSystemException;

}
