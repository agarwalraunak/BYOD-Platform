/**
 * 
 */
package com.kerberos.device.rest.representation.kerberos.service;

import java.util.Map;

/**
 * @author raunak
 *
 */
public class KeyServerResponse {
	
	private String encResponseAuthenticator;		//Encrypted with Service Session ID
	private Map<String, String> responseData;		//Encrypted with Service Session ID
	
	/**
	 * @return the encResponseAuthenticator
	 */
	public String getEncResponseAuthenticator() {
		return encResponseAuthenticator;
	}
	/**
	 * @param encResponseAuthenticator the encResponseAuthenticator to set
	 */
	public void setEncResponseAuthenticator(String encResponseAuthenticator) {
		this.encResponseAuthenticator = encResponseAuthenticator;
	}
	/**
	 * @return the responseData
	 */
	public Map<String, String> getResponseData() {
		return responseData;
	}
	/**
	 * @param responseData the responseData to set
	 */
	public void setResponseData(Map<String, String> responseData) {
		this.responseData = responseData;
	}
}
