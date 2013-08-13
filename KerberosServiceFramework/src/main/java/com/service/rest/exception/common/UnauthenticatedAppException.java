/**
 * 
 */
package com.service.rest.exception.common;

import javax.ws.rs.core.Response;

import com.service.rest.exception.RestException;

/**
 * @author raunak
 *
 */
public class UnauthenticatedAppException extends RestException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8158808868106184237L;
	private final static String message = "Can't find session related to given App Login Name. Unauthorized request";
	private final static int errorID = Response.Status.UNAUTHORIZED.getStatusCode();
	
	public UnauthenticatedAppException() {
		super(message, errorID);
	}
	

}
