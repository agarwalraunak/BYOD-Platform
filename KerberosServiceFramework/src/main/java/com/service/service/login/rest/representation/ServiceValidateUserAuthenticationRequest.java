/**
 * 
 */
package com.service.service.login.rest.representation;

/**
 * @author raunak
 *
 */
public class ServiceValidateUserAuthenticationRequest {
	
	/*
	 * Encrypted with App Login Session ID
	 */
	private String encUserLoginSessionID;
	private String appID;
	/*
	 * Encrypted with App Login Session ID
	 */
	private String encAuthenticator;
	/**
	 * @return the encUserLoginSessionID
	 */
	public String getEncUserLoginSessionID() {
		return encUserLoginSessionID;
	}
	/**
	 * @param encUserLoginSessionID the encUserLoginSessionID to set
	 */
	public void setEncUserLoginSessionID(String encUserLoginSessionID) {
		this.encUserLoginSessionID = encUserLoginSessionID;
	}
	/**
	 * @return the appID
	 */
	public String getAppID() {
		return appID;
	}
	/**
	 * @param appID the appID to set
	 */
	public void setAppID(String appID) {
		this.appID = appID;
	}
	/**
	 * @return the encAuthenticator
	 */
	public String getEncAuthenticator() {
		return encAuthenticator;
	}
	/**
	 * @param encAuthenticator the encAuthenticator to set
	 */
	public void setEncAuthenticator(String encAuthenticator) {
		this.encAuthenticator = encAuthenticator;
	}
	
	
	

}
