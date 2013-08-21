/**
 * 
 */
package com.device.kerberos.rest.api;

import java.io.IOException;
import java.util.Map;

import com.device.exception.RestClientException;
import com.device.kerberos.rest.api.KerberosAuthenticationAPIImpl.ServiceTicketResponseAttributes;

/**
 * This interface is used by <code>IKerberosRequestServiceTicketClient</code> to get the 
 * <code>ServiceTicket</code> from the <strong>Kerberos Server</strong> 
 * 
 * @author raunak
 *
 */
public interface IKerberosServiceTicketRequestAPI {

	/**
	 * @param <code>String</code> url of the Kerberos Server to be called to get the <code>ServiceTicket</code>
	 * @param encAppTGTPacket
	 * <code>String</code> TGT Packet 
	 * @param serviceName
	 * <code>String</code> Service UID (as registered in the Directory) for which the <code>ServiceTicket</code>
	 * has to be retrieved 
	 * @param sessionKey
	 * <code>String</code> Kerberos Session ID generated after <strong>Kerberos Authentication</strong>
	 * @return
	 * @throws IOException
	 * In case there are some errors encountered while retrieving information
	 * @throws RestClientException
	 * If the status of the response is not <strong>200</strong>. The server side error message and error 
	 * response code can be accessed using <code>getMessage</code> and <code>getErrorCode</code> methods respectively
	 */
	Map<ServiceTicketResponseAttributes, String> requestServiceTicketForApp(
			String url, String encAppTGTPacket, String serviceName,
			String sessionKey) throws IOException,
			RestClientException;

}
