package com.login.service.rest.representation;

import java.util.Map;

import com.login.rest.representation.RestServiceResponse;

/**
 * @author raunak
 *
 */
public class AppAccessServiceResponse extends RestServiceResponse {

	private Map<String, String> encResponseData;
	
	/**
	 * @return
	 * <code>Map<String, String></code> Response Data encrypted with <strong>App Session ID</strong>
	 */
	public Map<String, String> getEncResponseData() {
		return encResponseData;
	}
	/**
	 * @param 
	 * <code>Map<String, String></code> Response Data encrypted with <strong>App Session ID</strong>
	 */
	public void setEncResponseData(Map<String, String> encResponseData) {
		this.encResponseData = encResponseData;
	}
	
	
	
}
