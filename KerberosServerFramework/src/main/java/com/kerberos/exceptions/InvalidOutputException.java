/**
 * 
 */
package com.kerberos.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * @author raunak
 *
 */
public class InvalidOutputException extends WebApplicationException {
	
	public InvalidOutputException(String message, Response.Status status, String mediaType) {
		super(Response.status(status).entity(message).type(mediaType).build());
	}

}
