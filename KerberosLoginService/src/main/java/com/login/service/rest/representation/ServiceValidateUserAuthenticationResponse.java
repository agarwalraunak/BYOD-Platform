/**
 * 
 */
package com.login.service.rest.representation;

/**
 * @author raunak
 *
 */
public class ServiceValidateUserAuthenticationResponse {
	
	/*
	 * Encrypted using App Login Session ID
	 */
	private String encIsAuthenticated;
	private String encResponseAuthenticator;
	/**
	 * @return the encIsAuthenticated
	 */
	public String getEncIsAuthenticated() {
		return encIsAuthenticated;
	}
	/**
	 * @param encIsAuthenticated the encIsAuthenticated to set
	 */
	public void setEncIsAuthenticated(String encIsAuthenticated) {
		this.encIsAuthenticated = encIsAuthenticated;
	}
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
	
	

}
