/**
 * 
 */
package com.device.exception;

/**
 * Thrown in case the decryption of the Response attributes fails or response is not valid
 * 
 * @author raunak
 *
 */
public class UnauthorizedResponseException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5842874647776904295L;
	private static final String errorMessage = " Invalid Response returned from server of Type "; 
	private String message;
	
	public UnauthorizedResponseException(Class<?> responseClass, String methodName, Class<?> errorClass){
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
