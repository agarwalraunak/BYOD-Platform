/**
 * 
 */
package com.service.kerberos.rest.api;

import java.util.Map;

import com.service.exception.common.InternalSystemException;
import com.service.kerberos.rest.api.KerberosAuthenticationAPIImpl.ServiceTicketResponseAttributes;

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
	 * @throws InternalSystemException 
	 */
	Map<ServiceTicketResponseAttributes, String> requestServiceTicketForApp(
			String url, String encAppTGTPacket, String serviceName,
			String sessionKey) throws InternalSystemException;

}
