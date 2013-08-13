/**
 * 
 */
package com.service.kerberos.rest.client;

import com.service.model.kerberos.KerberosAppSession;
import com.service.model.kerberos.ServiceTicket;
import com.service.rest.exception.common.InternalSystemException;

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
