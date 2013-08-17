/**
 * 
 */
package com.service.service.rest.api;

import java.util.Date;

import javax.crypto.SecretKey;

import com.service.app.rest.representation.AppAuthenticationRequest;
import com.service.model.kerberos.ServiceTicket;
import com.service.model.service.ServiceSession;

/**
 * @author raunak
 *
 */
public interface IServiceAppAuthenticationAPI {
	

	/**
	 * @param serviceSessionKey
	 * @param serviceTicketPacket
	 * @param requestAuthenticator
	 * @return AppAuthenticationRequest or null if the input is invalid
	 */
	AppAuthenticationRequest createAppAuthenticationRequest(
			SecretKey serviceSessionKey, String serviceTicketPacket,
			String requestAuthenticator) ;

	/**
	 * @param encAppSessionID
	 * @param encResponseAuthenticator
	 * @param requestAuthenticator
	 * @param serviceTicket
	 * @param serviceSessionKey
	 * @return ServiceSession or null if the input is invalid or authenticator don't validate
	 */
	ServiceSession processAuthenticateAppResponse(String encAppSessionID,
			String encResponseAuthenticator, String encExpiryTime, Date requestAuthenticator,
			ServiceTicket serviceTicket, SecretKey serviceSessionKey);


}
