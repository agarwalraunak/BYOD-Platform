/**
 * 
 */
package com.login.exception.common;

import javax.ws.rs.core.Response;

import com.login.exception.RestException;

/**
 * @author raunak
 *
 */
public class UserDoesNotExistException extends RestException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2066791799414494431L;
	private final static String message = "User with the given username does not exist";
	private final static int errorID = Response.Status.UNAUTHORIZED.getStatusCode();
	
	public UserDoesNotExistException() {
		super(message, errorID);
	}
	

}
