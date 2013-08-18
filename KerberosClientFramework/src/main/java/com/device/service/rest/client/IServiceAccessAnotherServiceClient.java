package com.device.service.rest.client;

import java.io.IOException;
import java.util.Map;

import com.device.exception.InvalidResponseAuthenticatorException;
import com.device.exception.RestClientException;
import com.device.service.model.AppSession;
import com.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

/**
 * This interface provides the functionality to create <strong>Transfer Data</strong> over the secure connection established independent of the User
 * 
 * @author raunak
 *
 */
public interface IServiceAccessAnotherServiceClient {

	
	/**
	 * This method creates an <code>AppAccessServiceReuqest</code> over a secure connection which could be used to transfer data between applications.
	 * It requires the <strong>Client Application</strong> has been authenticated by the <strong>Serving Application</strong>
	 * @param url
	 * <code>String</code> URL of the Web service of the Service Application 
	 * @param requestMethod
	 * <code>com.service.util.connectionmanager.ConnectionManagerImpl.RequestMethod</code> Type of method to be used to contact the web service
	 * @param contentType: Content Type to be used to contact the web service
	 * <code>com.service.util.connectionmanager.ConnectionManagerImpl.ContentType</code> Type of content to be used to contact the web service
	 * @param serviceSessionID
	 * <code>String</code> Kerberos Service Session ID created by the kerberos for the connection between the service application
	 * @param serviceSession
	 * <code>AppSession</code> Application Session created by the application being accessed
	 * @param requestData
	 * <code>Map<String, String></code> Data to be send to the other service application
	 * @return
	 * <code>Map<String, String></code> Response Data sent by the Service
	 * @throws InvalidResponseAuthenticatorException 
	 * @throws RestClientException 
	 * @throws IOException
	 * In case there are some errors encountered while retrieving information
	 */
	Map<String, String> contactAnotherService(String url,
			RequestMethod requestMethod, ContentType contentType,
			String serviceSessionID, AppSession serviceSession,
			Map<String, String> requestData) throws InvalidResponseAuthenticatorException, IOException, RestClientException;

}
