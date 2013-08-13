/**
 * 
 */
package com.service.app.rest.representation;

/**
 * @author raunak
 *
 */
public class UserServiceAuthenticationRequest {
	
	/*
	 * Login Service User Session ID encrypted 
	 * with App Session ID for the Service Being accessed
	*/
	private String encUserSessionID;
	/*
	 * Ecnrypted using App Session ID for the Service Being accessed
	 */
	private String encAuthenticator;
	/*
	 * Ecnrypted using App Session ID for the Service Being accessed
	 */
	private String encUsername;
	private String appID;
	
	/**
	 * @return the encUsername
	 */
	public String getEncUsername() {
		return encUsername;
	}
	/**
	 * @param encUsername the encUsername to set
	 */
	public void setEncUsername(String encUsername) {
		this.encUsername = encUsername;
	}
	/**
	 * @return the encUserSessionID
	 */
	public String getEncUserSessionID() {
		return encUserSessionID;
	}
	/**
	 * @param encUserSessionID the encUserSessionID to set
	 */
	public void setEncUserSessionID(String encUserSessionID) {
		this.encUserSessionID = encUserSessionID;
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