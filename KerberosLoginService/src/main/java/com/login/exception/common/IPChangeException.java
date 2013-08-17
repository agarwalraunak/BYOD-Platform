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
public class IPChangeException extends RestException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3156552529711377422L;
	private final static String message = "Access from unknown location found. Request Forbidden";
	private final static int errorID = Response.Status.FORBIDDEN.getStatusCode();
	
	public IPChangeException() {
		super(message, errorID);
	}

	

}
