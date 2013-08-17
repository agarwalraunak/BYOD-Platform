/**
 * 
 */
package com.device.service.rest.api;

import java.util.Date;

import javax.crypto.SecretKey;

import com.device.exception.ResponseDecryptionException;
import com.device.kerberos.model.ServiceTicket;
import com.device.service.model.AppSession;
import com.device.service.rest.representation.AppAuthenticationRequest;

/**
 * @author raunak
 *
 */
public interface IServiceAppAuthenticationAPI {
	

	/**
	 * @param serviceSessionKey
	 * @param serviceTicketPacket
	 * @param requestAuthenticator
	 * @return
	 */
	AppAuthenticationRequest createAppAuthenticationRequest(
			SecretKey serviceSessionKey, String serviceTicketPacket,
			String requestAuthenticator) ;


	/**
	 * @param encAppSessionID
	 * @param encResponseAuthenticator
	 * @param encExpiryTime
	 * @param requestAuthenticator
	 * @param serviceTicket
	 * @param serviceSessionKey
	 * @return
	 * @throws ResponseDecryptionException
	 */
	AppSession processAuthenticateAppResponse(String encAppSessionID,
			String encResponseAuthenticator, String encExpiryTime,
			Date requestAuthenticator, ServiceTicket serviceTicket,
			SecretKey serviceSessionKey) throws ResponseDecryptionException;


}
