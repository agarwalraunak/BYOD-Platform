/**
 * 
 */
package com.login.exception;

/**
 * @author raunak
 *
 */
public class ResponseDecryptionException extends Exception{
	
	
	private static final long serialVersionUID = 5941407490548886930L;
	private static final String errorMessage = " Failed to decrypt Response Attributes of Type "; 
	private String message;

	/**
	 * @param responseClass
	 * @param methodName
	 * @param errorClass
	 */
	public ResponseDecryptionException(Class<?> responseClass, String methodName, Class<?> errorClass) {
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
