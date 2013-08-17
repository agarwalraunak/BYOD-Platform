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
public class UnauthenticatedUserException extends RestException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3913022952451286965L;
	private final static String message = "Can't find session related to given User. Unauthorized request";
	private final static int errorID = Response.Status.UNAUTHORIZED.getStatusCode();
	
	public UnauthenticatedUserException() {
		super(message, errorID);
	}
	

}
