/**
 * 
 */
package com.device.exception;

import com.device.error.ErrorResponse;

/**
 * Thrown in case if there is error encountered on the Server side
 * 
 * @author raunak
 *
 */
public class RestClientException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1704069471353274816L;
	private String errorMessage;
	private int errorCode;
	
	public RestClientException(ErrorResponse errorResponse) {
		super();
		this.errorCode = errorResponse.getErrorId();
		this.errorMessage = errorResponse.getErrorMessage();
	}
	/**
	 * @return the errorMessage from the server side
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @return the response status code as given by the server side
	 */
	public int getErrorCode() {
		return errorCode;
	}
	
	
	
	
	

}
