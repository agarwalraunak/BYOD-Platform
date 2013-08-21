/**
 * 
 */
package com.service.kerberos.rest.api;

import java.util.Map;

import com.service.exception.common.InternalSystemException;
import com.service.kerberos.rest.api.KerberosAuthenticationAPIImpl.ServiceTicketResponseAttributes;

/**
 * This interface is used by <code>IKerberosRequestServiceTicketClient</code> to get the 
 * <code>ServiceTicket</code> from the <strong>Kerberos Server</strong>
 * 
 * @author raunak
 *
 */
public interface IKerberosServiceRequestAPI {

	/**
	 * @param 
	 * <code>String</code> url of the Kerberos Server to be called to get the <code>ServiceTicket</code>
	 * @param encAppTGTPacket
	 * <code>String</code> TGT Packet 
	 * @param serviceName
	 * <code>String</code> Service UID (as registered in the Directory) for which the <code>ServiceTicket</code>
	 * has to be retrieved 
	 * @param sessionKey
	 * <code>String</code> Kerberos Session ID generated after <strong>Kerberos Authentication</strong>
	 * @return
	 * <code>Map<ServiceTicketResponseAttrubutes, String></code>
	 * @throws InternalSystemException
	 * If there are any errors encountered 
	 */
	Map<ServiceTicketResponseAttributes, String> requestServiceTicketForApp(
			String url, String encAppTGTPacket, String serviceName,
			String sessionKey) throws InternalSystemException;

}
