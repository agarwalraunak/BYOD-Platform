/**
 * 
 */
package com.device.exception;

/**
 * @author raunak
 *
 */
public class UnauthenticatedUserException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5842874647776904295L;
	private static final String errorMessage = "Action Invalid. Unauthenticated user found. No Session exists for the user "; 
	private String message;
	
	public UnauthenticatedUserException(String username){
		super(errorMessage+username);
		message = errorMessage+username;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	

}
