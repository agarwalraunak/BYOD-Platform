/**
 * 
 */
package com.service.kerberos.rest.client;

import javax.crypto.SecretKey;

import com.service.kerberos.rest.api.KerberosAuthenticationAPIImpl.SecretKeyType;
import com.service.model.kerberos.ServiceTicket;

/**
 * @author raunak
 *
 */
public interface IKerberosKeyServerClient {

	/**
	 * @param serviceTicket
	 * @param keyType
	 * @return SecretKey or null in case exception or error or if key is not available on the kerberos server
	 */
	SecretKey getKeyFromKeyServer(ServiceTicket serviceTicket,
			SecretKeyType keyType);

}
