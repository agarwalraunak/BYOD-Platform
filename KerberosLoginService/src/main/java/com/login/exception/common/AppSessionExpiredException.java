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
public class AppSessionExpiredException extends RestException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2570544664520760756L;
	private final static String message = "App Session has Expired";
	private final static int errorID = Response.Status.UNAUTHORIZED.getStatusCode();
	
	public AppSessionExpiredException() {
		super(message, errorID);
	}

	

}
