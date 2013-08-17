/**
 * 
 */
package com.login.kerberos.rest.api;

import java.io.IOException;
import java.util.Map;

import com.login.exception.RestClientException;
import com.login.kerberos.rest.api.KerberosAuthenticationAPIImpl.ServiceTicketResponseAttributes;

/**
 * @author raunak
 *
 */
public interface IKerberosServiceRequestAPI {

	/**
	 * @param url
	 * @param encAppTGTPacket
	 * @param serviceName
	 * @param sessionKey
	 * @return
	 * @throws RestClientException 
	 * @throws IOException 
	 */
	Map<ServiceTicketResponseAttributes, String> requestServiceTicketForApp(
			String url, String encAppTGTPacket, String serviceName,
			String sessionKey) throws IOException, RestClientException;

}
