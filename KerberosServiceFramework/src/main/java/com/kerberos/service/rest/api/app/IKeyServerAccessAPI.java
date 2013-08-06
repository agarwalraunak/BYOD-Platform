/**
 * 
 */
package com.kerberos.service.rest.api.app;

import com.kerberos.service.models.ServiceTicket;
import com.kerberos.service.rest.exceptions.ServiceUnavailableException;

/**
 * @author raunak
 *
 */
public interface IKeyServerAccessAPI {

	/**
	 * @return
	 * @throws ServiceUnavailableException
	 */
	ServiceTicket getKeyServerServiceTicketForApp()
			throws ServiceUnavailableException;

}
