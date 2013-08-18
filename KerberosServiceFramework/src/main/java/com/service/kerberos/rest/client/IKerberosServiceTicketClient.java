/**
 * 
 */
package com.service.kerberos.rest.client;

import com.service.exception.common.InternalSystemException;
import com.service.model.kerberos.KerberosAppSession;
import com.service.model.kerberos.ServiceTicket;

/**
 * This interface allows to get the <code>ServiceTicket</code> from the Kerberos Server
 * for the service to be invoked by the Application
 * @author raunak
 *
 */
public interface IKerberosServiceTicketClient {

	/**
	 * This method retrieves the <code>ServiceTicket</code> from the Kerberos Server.
	 * It requires <code>IKerberosAuthenticationClient kerberosAuthentication()</code> method to be
	 * called before this
	 * @param serviceName
	 * <code>String</code> UID (as given in the Apache DS) of the service for which <code>ServiceTicket</code> has to be fetched 
	 * @param appSession
	 * <code>KerberosAppSession</code> of the Application for whom the <code>ServiceTicket</code> is being fetched
	 * @return
	 * <code>ServiceTicket</code> or null if the app is not authenticated
	 * @param serviceName
	 * @param appSession
	 * @throws InternalSystemException
	 * In case there is some error encountered either on the Kerberos Server side or in the response
	 * return Internal System Exception to the Client 
	 */
	ServiceTicket getServiceTicketForApp(String serviceName,
			KerberosAppSession appSession) throws InternalSystemException;

}
