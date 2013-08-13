/**
 * 
 */
package com.device.exception;

/**
 * @author raunak
 *
 */
public class InvalidMethodArgumentValue extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	
	/**
	 * @param methodName
	 */
	public InvalidMethodArgumentValue(String methodName) {
		super("Invalid Input parameter provided to "+methodName);
		this.message = "Invalid Input parameter provided to "+methodName;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	

}
