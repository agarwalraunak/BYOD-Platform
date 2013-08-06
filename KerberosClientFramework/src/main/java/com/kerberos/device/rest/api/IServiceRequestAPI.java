/**
 * 
 */
package com.kerberos.device.rest.api;

import java.io.IOException;
import java.util.Map;

import javax.management.InvalidAttributeValueException;

import com.kerberos.device.rest.api.AuthenticationAPIImpl.ServiceTicketResponseAttributes;

/**
 * @author raunak
 *
 */
public interface IServiceRequestAPI {

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
