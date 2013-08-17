package com.device.service.rest.client;

import java.io.IOException;

import com.device.exception.ResponseDecryptionException;
import com.device.exception.RestClientException;
import com.device.kerberos.model.ServiceTicket;
import com.device.service.model.AppSession;

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
	 * <code>AppSession</code> 
	 * @throws IOException
	 * In case there are some errors encountered while retrieving information
	 * @throws RestClientException
	 * If the status of the response is not <strong>200</strong>. The server side error message and error 
	 * response code can be accessed using <code>getMessage</code> and <code>getErrorCode</code> methods respectively
	 * @throws ResponseDecryptionException 
	 * If the Application was unable to decrypt the Response sent by the server
	 */
	AppSession authenticateAppServiceTicket(String url,
			ServiceTicket serviceTicket) throws IOException, RestClientException, ResponseDecryptionException;

}
