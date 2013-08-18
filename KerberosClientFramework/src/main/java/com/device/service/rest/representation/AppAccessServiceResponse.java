package com.device.service.rest.representation;

import java.util.Map;

/**
 * @author raunak
 *
 */
public class AppAccessServiceResponse {

	/*
	 * Attributes encrypted using App Session ID
	 */
	private String encResponseAuthenticator;
	private Map<String, String> encResponseData;
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
	 * @return the encResponseData
	 */
	public Map<String, String> getEncResponseData() {
		return encResponseData;
	}
	/**
	 * @param encResponseData the encResponseData to set
	 */
	public void setEncResponseData(Map<String, String> encResponseData) {
		this.encResponseData = encResponseData;
	}
	
	
	
}
