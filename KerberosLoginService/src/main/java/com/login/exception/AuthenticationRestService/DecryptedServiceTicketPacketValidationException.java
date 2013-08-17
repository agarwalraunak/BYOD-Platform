/**
 * 
 */
package com.login.exception.AuthenticationRestService;

import javax.ws.rs.core.Response;

import com.login.exception.RestException;

/**
 * @author raunak
 *
 */
public class DecryptedServiceTicketPacketValidationException extends RestException{

	private static final long serialVersionUID = 5208316255360621071L;
	private final static String message = "Service Ticket Packet failed to decrypt. Unauthorized request";
	private final static int errorID = Response.Status.UNAUTHORIZED.getStatusCode();
	
	public DecryptedServiceTicketPacketValidationException() {
		super(message, errorID);
	}
	

}
