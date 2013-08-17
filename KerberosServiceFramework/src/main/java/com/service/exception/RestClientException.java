/**
 * 
 */
package com.service.exception;

import com.service.error.ErrorResponse;

/**
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
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @return the errorCode
	 */
	public int getErrorCode() {
		return errorCode;
	}
	
	
	
	
	

}
