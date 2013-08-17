/**
 * 
 */
package com.service.kerberos.rest.client;

import java.io.IOException;

import com.service.exception.ApplicationDetailServiceUninitializedException;
import com.service.exception.ResponseDecryptionException;
import com.service.exception.RestClientException;
import com.service.model.kerberos.KerberosAppSession;

/**
 * @author raunak
 *
 */
public interface IKerberosAuthenticationClient {

	/**
	 * @return
	 * @throws IOException
	 * @throws RestClientException
	 * @throws ResponseDecryptionException
	 * @throws ApplicationDetailServiceUninitializedException
	 */
	KerberosAppSession kerberosAuthentication() throws IOException, RestClientException,
			ResponseDecryptionException,
			ApplicationDetailServiceUninitializedException;

}
