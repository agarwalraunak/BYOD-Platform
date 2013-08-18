/**
 * 
 */
package com.service.kerberos.rest.client;

import javax.crypto.SecretKey;

import com.service.kerberos.rest.api.KerberosAuthenticationAPIImpl.SecretKeyType;
import com.service.model.kerberos.ServiceTicket;

/**
 * This interface provides the functionality to securely connect with <strong>Key Server</strong>
 * to get Secret Keys
 * 
 * @author raunak
 *
 */
public interface IKerberosKeyServerClient {

	/**
	 * This method retrieves the <code>SecretKey</code> from <strong>Key Server</code>. It requires the 
	 * <code>ServiceTicket</code> for the Key Server  
	 * @param serviceTicket
	 * <code>ServiceTicket</code> Kerberos Service Ticket to access the <strong>Key Server</strong>
	 * @param keyType
	 * <code>SecretKeyType</code> Type of key to be fetched from <strong>Key Server</strong>
	 * @return 
	 * <code>SecretKey</code> or null in case exception or error or if key is not available on the kerberos server
	 */
	SecretKey getKeyFromKeyServer(ServiceTicket serviceTicket,
			SecretKeyType keyType);

}
