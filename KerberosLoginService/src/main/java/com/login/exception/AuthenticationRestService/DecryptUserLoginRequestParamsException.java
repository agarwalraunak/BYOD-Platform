package com.login.exception.AuthenticationRestService;

import javax.ws.rs.core.Response;

import com.login.exception.RestException;

public class DecryptUserLoginRequestParamsException extends RestException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3669390063231808316L;
	private final static String message = "User Login Request parameters failed to decrypt. Unauthorized request";
	private final static int errorID = Response.Status.UNAUTHORIZED.getStatusCode();
	
	public DecryptUserLoginRequestParamsException() {
		super(message, errorID);
	}

}
