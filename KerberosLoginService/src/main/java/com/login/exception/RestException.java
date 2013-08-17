/**
 * 
 */
package com.login.exception;


/**
 * @author raunak
 *
 */
public class RestException extends Exception{


	/**
	 * 
	 */
	private static final long serialVersionUID = -8856441808643847831L;
	private String message;
	private int errorID;
	
	public RestException(String message, int errorID) {
		super(message);
		this.message = message;
		this.errorID = errorID;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the errorID
	 */
	public int getErrorID() {
		return errorID;
	}
	
	
	
	

}
