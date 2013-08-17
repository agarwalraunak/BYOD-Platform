/**
 * 
 */
package com.login.app.rest.representation;

import com.login.rest.representation.RestServiceResponse;


/**
 * @author raunak
 *
 */
public class AppAuthenticationResponse extends RestServiceResponse{
	
	private String encAppSessionID;					//Encrypted with Service Key
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
