/**
 * 
 */
package com.login.kerberos.rest.client;

import java.io.IOException;

import javax.crypto.SecretKey;

import com.login.exception.RestClientException;
import com.login.kerberos.rest.api.KerberosAuthenticationAPIImpl.SecretKeyType;
import com.login.model.kerberos.ServiceTicket;

/**
 * This interface provides the functionality to get the key from the key server 
 * 
 * @author raunak
 *
 */
public interface IKerberosKeyServerClient {

	/**
	 * This method can be used to get the <code>SecretKey</code> from <strong>Key Server</strong>. 
	 *  It requires to get the <code>ServiceTicket</code> for the Key Server
	 * @param serviceTicket
	 * <code>ServiceTicket</code> Kerberos Service Ticket to access the Key Server 
	 * @param keyType
	 * <code>SecretKeyType</code> Type of key to be retrieved from the key server
	 * @return 
	 * <code>SecretKey</code> 
	 * @throws RestClientException 
	 * If the status of the response is not <strong>200</strong>. The server side error message and error 
	 * response code can be accessed using <code>getMessage</code> and <code>getErrorCode</code> methods respectively
	 * @throws IOException 
	 * In case there are some errors encountered while retrieving information
	 */
	SecretKey getKeyFromKeyServer(ServiceTicket serviceTicket,
			SecretKeyType keyType) throws RestClientException, IOException;

}
