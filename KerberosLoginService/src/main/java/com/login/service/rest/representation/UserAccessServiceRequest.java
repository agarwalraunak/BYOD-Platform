package com.login.service.rest.representation;

import java.util.Map;

import com.login.rest.representation.UserRestServiceRequest;

public class UserAccessServiceRequest extends UserRestServiceRequest{
	
	private Map<String, String> data;
	/**
	 * @return the data
	 * <code>Map<String, String></code> data encrypted using <strong>User Session ID</strong>
	 */
	public Map<String, String> getData() {
		return data;
	}
	/**
	 * @param 
	 * <code>Map<String, String></code> data encrypted using <strong>User Session ID</strong>
	 */
	public void setData(Map<String, String> data) {
		this.data = data;
	}
}
