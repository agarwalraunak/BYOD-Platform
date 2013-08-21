/**
 * 
 */
package com.device.exception;

/**
 * Thrown if the <strong>Response Authenticator</strong> fails to validate
 * 
 * @author raunak
 *
 */
public class InvalidResponseAuthenticatorException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5842874647776904295L;
	private static final String errorMessage = ": Invalid Response Authenticator returned from server of Type: "; 
	private String message;
	
	public InvalidResponseAuthenticatorException(Class<?> responseClass, String methodName, Class<?> errorClass){
		super(errorMessage+responseClass.getName()+" in "+errorClass.getName()+" "+methodName);
		message = errorMessage+responseClass.getName()+" in "+errorClass.getName()+" "+methodName;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	

}
