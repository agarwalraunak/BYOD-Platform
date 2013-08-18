/**
 * 
 */
package com.device.service.rest.representation;


/**
 * @author raunak
 *
 */
public class AppAuthenticationResponse {
	
	private String encAppSessionID;					//Encrypted with Service Key
	private String encResponseAuthenticator; 	//Encrypted using Kerberos Service Session ID
	private String encExpiryTime;
	/**
	 * @return the encAppSessionID
	 */
	public String getEncAppSessionID() {
		return encAppSessionID;
	}
	/**
	 * @param encAppSessionID the encAppSessionID to set
	 */
	public void setEncAppSessionID(String encAppSessionID) {
		this.encAppSessionID = encAppSessionID;
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
	/**
	 * @return the encExpiryTime
	 */
	public String getEncExpiryTime() {
		return encExpiryTime;
	}
	/**
	 * @param encExpiryTime the encExpiryTime to set
	 */
	public void setEncExpiryTime(String encExpiryTime) {
		this.encExpiryTime = encExpiryTime;
	}
	
}
