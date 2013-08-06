/**
 * 
 */
package com.kerberos.service.rest.api.kerberos;

import java.io.IOException;
import java.util.Map;

import javax.management.InvalidAttributeValueException;

import com.kerberos.service.models.ServiceTicket;
import com.kerberos.service.rest.api.kerberos.KerberosAuthenticationAPIImpl.ServiceTicketResponseAttributes;
import com.kerberos.service.rest.exceptions.ServiceUnavailableException;

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

	/**
	 * @param serviceName
	 * @return
	 * @throws ServiceUnavailableException
	 */
	ServiceTicket checkAppAuthenticationAndGetServiceTicket(String serviceName)
			throws ServiceUnavailableException;

}
