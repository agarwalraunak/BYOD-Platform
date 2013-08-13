/**
 * 
 */
package com.service.kerberos.rest.client;

import com.service.model.kerberos.KerberosAppSession;
import com.service.rest.exception.common.InternalSystemException;

/**
 * @author raunak
 *
 */
public interface IKerberosAuthenticationClient {

	/**
	 * @return KerberosAppSession 
	 * @throws InternalSystemException
	 */
	KerberosAppSession kerberosAuthentication() throws InternalSystemException;

}
