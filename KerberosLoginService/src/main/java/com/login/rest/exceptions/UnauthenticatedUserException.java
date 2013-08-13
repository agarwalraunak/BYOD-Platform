/**
 * 
 */
package com.login.rest.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * @author raunak
 *
 */
public class UnauthenticatedUserException extends WebApplicationException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnauthenticatedUserException(String message, Response.Status status, String mediaType) {
        super(Response.status(status).entity(message).type(mediaType).build());
    }

}
