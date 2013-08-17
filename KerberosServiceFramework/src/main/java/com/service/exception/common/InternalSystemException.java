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

public class InternalSystemException extends RestException{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2894945187034700224L;
	private final static String message = "System Encountered Problem while process the request. Please try again later";
	private final static int errorID = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
	
	public InternalSystemException() {
		super(message, errorID);
	}
}
