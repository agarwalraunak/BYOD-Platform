/**
 * 
 */
package com.device.kerberos.rest.api;

import java.io.IOException;
import java.util.Map;

import javax.management.InvalidAttributeValueException;

import com.device.kerberos.rest.api.KerberosAppAuthenticationAPIImpl.ServiceTicketResponseAttributes;

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
	 * @throws IOException
	 * @throws InvalidAttributeValueException
	 */
	Map<ServiceTicketResponseAttributes, String> requestServiceTicketForApp(
			String url, String encAppTGTPacket, String serviceName,
			String sessionKey) throws IOException,
			InvalidAttributeValueException;

}
