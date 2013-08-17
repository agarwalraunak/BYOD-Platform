package com.login.kerberos.rest.client;

import java.io.IOException;

import com.login.exception.RestClientException;
import com.login.model.kerberos.KerberosAppSession;
import com.login.model.kerberos.ServiceTicket;

/**
 * This interface allows to get the <code>ServiceTicket</code> from the Kerberos Server
 * for the service to be invoked by the Application
 * 
 * @author raunak
 *
 */
public interface IKerberosRequestServiceTicketClient {


	/**
	 * This method retrieves the <code>ServiceTicket</code> from the Kerberos Server.
	 * It requires <code>IKerberosAuthenticationClient kerberosAuthentication()</code> method to be
	 * called before this
	 * @param serviceName
	 * <code>String</code> UID (as given in the Apache DS) of the service for which <code>ServiceTicket</code> has to be fetched 
	 * @param appSession
	 * <code>KerberosAppSession</code> of the Application for whom the <code>ServiceTicket</code> is being fetched
	 * @return
	 * <code>ServiceTicket</code> or null if the input parameters are not valid
	 * @throws IOException
	 * In case there are some errors encountered while retrieving information
	 * @throws RestClientException
	 * If the status of the response is not <strong>200</strong>. The server side error message and error 
	 * response code can be accessed using <code>getMessage</code> and <code>getErrorCode</code> methods respectively
	 */
	ServiceTicket getServiceTicketForApp(String serviceName,
			KerberosAppSession appSession) throws IOException, RestClientException;

}
