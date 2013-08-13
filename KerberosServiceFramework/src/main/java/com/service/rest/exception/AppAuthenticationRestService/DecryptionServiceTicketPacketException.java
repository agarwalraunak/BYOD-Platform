/**
 * 
 */
package com.service.rest.exception.AppAuthenticationRestService;

import javax.ws.rs.core.Response;

import com.service.rest.exception.RestException;

/**
 * @author raunak
 *
 */
public class DecryptionServiceTicketPacketException extends RestException{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7762519636707790473L;
	private final static String message = "Service Ticket Packet failed to decrypt. Unauthorized request";
	private final static int errorID = Response.Status.UNAUTHORIZED.getStatusCode();
	
	public DecryptionServiceTicketPacketException() {
		super(message, errorID);
	}

}
