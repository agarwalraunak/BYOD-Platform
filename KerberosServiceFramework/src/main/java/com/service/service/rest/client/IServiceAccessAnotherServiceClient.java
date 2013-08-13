package com.service.service.rest.client;

import java.io.IOException;
import java.util.Map;

import com.service.model.kerberos.KerberosAppSession;
import com.service.model.service.ServiceSession;
import com.service.rest.exception.common.AuthenticatorValidationException;
import com.service.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.service.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

public interface IServiceAccessAnotherServiceClient {

	
	/**
	 * Provides a Client to create a secure connection between service applications. Data can be posted using the HashMap provided 
	 * @param url: URL of the Web service of the Service Application to be called 
	 * @param requestMethod: Request method to be used to contact the web service
	 * @param contentType: Content Type to be used to contact the web service
	 * @param serviceName: UID of the application being accessed
	 * @param kerberosAppSession: Kerberos Session of the application from which the application is being accessed
	 * @param serviceSessionID: Kerberos Service Session ID created by the kerberos for the connection between the service application
	 * @param serviceSession: Session created by the application being contacted
	 * @param requestData: Data to be send to the other service application
	 * @return
	 * @throws IOException
	 * @throws AuthenticatorValidationException 
	 */
	Map<String, String> contactAnotherService(String url,
			RequestMethod requestMethod, ContentType contentType,
			String serviceName, KerberosAppSession kerberosAppSession,
			String serviceSessionID, ServiceSession serviceSession,
			Map<String, String> requestData) throws IOException, AuthenticatorValidationException;

}
