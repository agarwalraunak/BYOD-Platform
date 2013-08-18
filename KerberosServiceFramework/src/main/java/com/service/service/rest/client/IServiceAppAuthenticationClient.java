package com.service.service.rest.client;

import com.service.exception.common.InternalSystemException;
import com.service.model.kerberos.ServiceTicket;
import com.service.model.service.ServiceSession;

/**
 * This interface provides the functionality to get the <strong>App</strong> authenticated by the 
 * <strong>Service</strong>
 * @author raunak
 *
 */
public interface IServiceAppAuthenticationClient {

	/**
	 * This method gets the <strong>App</strong> authenticated by the Service. It uses Kerberos Protocol
	 * to achieve mutual authentication between the App and the Service  
	 * @param url 
	 * <code>String</code> url of the web service to be invoked to authenticate App Service Ticket
	 * @param serviceTicket 
	 * <code>ServiceTicket</code> Kerberos Service Ticket required to access the service
	 * @return
	 * <code>ServiceSession </code> or null if fails to get the Authentication fails
	 * @throws InternalSystemException
	 */
	ServiceSession authenticateAppServiceTicket(String url,
			ServiceTicket serviceTicket) throws InternalSystemException;
}
