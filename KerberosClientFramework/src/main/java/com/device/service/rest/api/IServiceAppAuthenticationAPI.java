/**
 * 
 */
package com.device.service.rest.api;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

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
	 * @throws InvalidAttributeValueException
	 */
	AppAuthenticationRequest createAppAuthenticationRequest(
			SecretKey serviceSessionKey, String serviceTicketPacket,
			String requestAuthenticator) throws InvalidAttributeValueException;

	/**
	 * @param encAppSessionID
	 * @param encResponseAuthenticator
	 * @param requestAuthenticator
	 * @param serviceTicket
	 * @param serviceSessionKey
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	AppSession processAuthenticateAppResponse(String encAppSessionID,
			String encResponseAuthenticator, Date requestAuthenticator,
			ServiceTicket serviceTicket, SecretKey serviceSessionKey)
			throws InvalidAttributeValueException;


}
