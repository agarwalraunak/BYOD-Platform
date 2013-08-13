/**
 * 
 */
package com.device.rest.exceptions;


/**
 * @author raunak
 *
 */
public class UnauthorizedResponseException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnauthorizedResponseException(String message) {
		super(message);
    }
	
}
