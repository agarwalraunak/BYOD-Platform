package com.service.model.app;

import java.util.Date;

public class Response {
	
	private String responseCode;
	private Date responseAuthenticator;
	/**
	 * @return the responseCode
	 */
	public String getResponseCode() {
		return responseCode;
	}
	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(String responseCode) {
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
