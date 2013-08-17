package com.service.model.app;

import java.util.Date;

public class Request {
	
	private String path;
	private Date created;
	private Response response;
	private String requestAuthenticator;
	
	public Request() {
		created = new Date();
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @return the response
	 */
	public Response getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(Response response) {
		this.response = response;
	}

	/**
	 * @return the requestAuthenticator
	 */
	public String getRequestAuthenticator() {
		return requestAuthenticator;
	}

	/**
	 * @param requestAuthenticator the requestAuthenticator to set
	 */
	public void setRequestAuthenticator(String requestAuthenticator) {
		this.requestAuthenticator = requestAuthenticator;
	}
}
