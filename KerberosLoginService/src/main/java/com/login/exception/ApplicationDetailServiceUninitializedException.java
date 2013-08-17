/**
 * 
 */
package com.login.exception;

/**
 * @author raunak
 *
 */
public class ApplicationDetailServiceUninitializedException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8706805775095296214L;
	private String message;
	
	public ApplicationDetailServiceUninitializedException() {
		super("ApplicationDetailService has not been initialized");
		this.message = "ApplicationDetailService has not been initialized";
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	

}
