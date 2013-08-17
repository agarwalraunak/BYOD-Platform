/**
 * 
 */
package com.login.app.rest.api;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import com.login.app.rest.api.AppAuthenticationAPIImpl.ServiceTicketAttributes;
import com.login.app.rest.representation.AppAuthenticationResponse;
import com.login.model.app.AppSession;

/**
 * @author raunak
 *
 */
public interface IAppAuthenticationAPI {


	/**
	 * @param encServiceTicketPacket
	 * @param serviceKey
	 * @return  Map<ServiceTicketAttributes, String> or null if the decryption is not successfull
	 */
	Map<ServiceTicketAttributes, String> decryptServiceTicketPacket(
			String encServiceTicketPacket, SecretKey serviceKey);

	/**
	 * @param username
	 * @param serviceSessionID
	 * @param serviceTicketExpirationString
	 * @return boolean true if validation was successfull else false
	 */
	boolean validateServiceTicket(String username, String serviceSessionID,
			String serviceTicketExpirationString);

	/**
	 * @param appSession
	 * @param requestAuthenticator
	 * @param path
	 * @param serviceSessionKey
	 * @return
	 */
	AppAuthenticationResponse createAppAuthenticationResponse(
			AppSession appSession, Date requestAuthenticator, String path,
			SecretKey serviceSessionKey);

}
