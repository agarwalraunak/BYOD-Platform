/**
 * 
 */
package com.kerberos.service.rest.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * @author raunak
 *
 */
public class InvalidRequestException extends WebApplicationException {
	
	public InvalidRequestException(String message, Response.Status status, String mediaType) {
        super(Response.status(status).entity(message).type(mediaType).build());
    }

}
