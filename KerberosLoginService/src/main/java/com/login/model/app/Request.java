package com.login.model.app;

import java.util.Date;


public class Request {
	
	private String path;
	private Date created;
	private Response response;
	private Date requestAuthenticator;
	
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
	public Date getRequestAuthenticator() {
		return requestAuthenticator;
	}

	/**
	 * @param requestAuthenticator the requestAuthenticator to set
	 */
	public void setRequestAuthenticator(Date requestAuthenticator) {
		this.requestAuthenticator = requestAuthenticator;
	}
	
	/**
	 * @param request
	 * @param responseCode
	 * @param responseAuthenticator
	 */
	public void createResponse(int responseCode, Date responseAuthenticator){
		response = new Response();
		response.setResponseAuthenticator(responseAuthenticator);
		response.setResponseCode(responseCode);
	}
}
