/**
 * 
 */
package com.device.kerberos.rest.api;

import java.io.IOException;
import java.util.Map;

import com.device.exception.RestClientException;
import com.device.kerberos.rest.api.KerberosAuthenticationAPIImpl.ServiceTicketResponseAttributes;

/**
 * @author raunak
 *
 */
public interface IKerberosServiceTicketRequestAPI {

	/**
	 * @param url
	 * @param encAppTGTPacket
	 * @param serviceName
	 * @param sessionKey
	 * @return
	 * @throws IOException
	 * @throws RestClientException 
	 */
	Map<ServiceTicketResponseAttributes, String> requestServiceTicketForApp(
			String url, String encAppTGTPacket, String serviceName,
			String sessionKey) throws IOException,
			RestClientException;

}
