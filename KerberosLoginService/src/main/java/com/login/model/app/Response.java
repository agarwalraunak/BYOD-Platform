package com.login.model.app;

import java.util.Date;

public class Response {
	
	private int responseCode;
	private Date responseAuthenticator;
	/**
	 * @return the responseCode
	 */
	public int getResponseCode() {
		return responseCode;
	}
	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	/**
	 * @return the responseAuthenticator
	 */
	public Date getResponseAuthenticator() {
		return responseAuthenticator;
	}
	/**
	 * @param responseAuthenticator the responseAuthenticator to set
	 */
	public void setResponseAuthenticator(Date responseAuthenticator) {
		this.responseAuthenticator = responseAuthenticator;
	}

	
}
