package com.service.error;

/**
 * This model is used to capture the error given out by the Server end
 * 
 * @author raunak
 *
 */
public class ErrorResponse {
	
	private String errorMessage;
	private int errorId;

	
	/**
	 * @return the errorMessage returned by the server
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
	 * @return the Status Code of the error resposne
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
