/**
 * 
 */
package com.service.exception.common;

import javax.ws.rs.core.Response;

import com.service.exception.RestException;

/**
 * @author raunak
 *
 */
public class UserSessionExpiredException extends RestException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8250801988035578590L;
	private final static String message = "User Session has expired";
	private final static int errorID = Response.Status.UNAUTHORIZED.getStatusCode();
	
	public UserSessionExpiredException() {
		super(message, errorID);
	}
}
