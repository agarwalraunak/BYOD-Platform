/**
 * 
 */
package com.service.kerberos.rest.client;

import com.service.exception.common.InternalSystemException;
import com.service.model.kerberos.KerberosAppSession;
import com.service.model.kerberos.ServiceTicket;

/**
 * @author raunak
 *
 */
public interface IKerberosServiceTicketClient {

	/**
	 * @param serviceName
	 * @param appSession
	 * @return ServiceTicket or null in case app is not authenticated or method encounters any exception
	 * @throws InternalSystemException
	 */
	ServiceTicket getServiceTicketForApp(String serviceName,
			KerberosAppSession appSession) throws InternalSystemException;

}
