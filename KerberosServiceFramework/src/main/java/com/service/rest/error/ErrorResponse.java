/**
 * 
 */
package com.service.rest.error;

/**
 * @author raunak
 *
 */
public class ErrorResponse {
	
	private String errorMessage;
	private int errorId;

	
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the errorId
	 */
	public int getErrorId() {
		return errorId;
	}

	/**
	 * @param errorId the errorId to set
	 */
	public void setErrorId(int errorId) {
		this.errorId = errorId;
	}

	

}
