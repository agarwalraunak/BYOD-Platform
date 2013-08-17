package com.device.kerberos.rest.client;

import java.io.IOException;

import com.device.exception.ApplicationDetailServiceUninitializedException;
import com.device.exception.ResponseDecryptionException;
import com.device.exception.RestClientException;

/**
 * This interface provides the functionality of Kerberos Authentication for the Application.
 * Uses the <code>ApplicationDetailService</code> class to fetch the Application
 * registration information i.e. the Login name and Password 
 * 
 * @author raunak
 * 
 */
public interface IKerberosAuthenticationClient {

	/**
	 * This method checks for Application Authentication using the Kerberos Protocol.
	 * Uses the <code>ApplicationDetailService </code> class to fetch the Application
	 * registration information i.e. the Login name and Password. These information are
	 * used to get a TGT. This retrieve TGT is stored in the model <code>TGT</code>. If the 
	 * authentication is successful creates a <code>KerberosAppSession</code> which could be 
	 * retrived from <code>KerberosSessionManager getKerberosAppSession()</code> method   
	 * @return boolean
	 * <code>true</code> if the Authentication is successful otherwise, <code>false</code>
	 * @throws IOException
	 * In case there are some errors encountered while retrieving information
	 * @throws RestClientException
	 * If the status of the response is not <strong>200</strong>. The server side error message and error 
	 * response code can be accessed using <code>getMessage</code> and <code>getErrorCode</code> methods respectively
	 * @throws ResponseDecryptionException
	 * If the Application was unable to decrypt the Response sent by the server
	 * @throws ApplicationDetailServiceUninitializedException 
	 * In case if the <code>ApplicationDetailService</code> has not been configured properly
	 */
	boolean kerberosAuthentication() throws IOException, RestClientException, ResponseDecryptionException, ApplicationDetailServiceUninitializedException;

}
