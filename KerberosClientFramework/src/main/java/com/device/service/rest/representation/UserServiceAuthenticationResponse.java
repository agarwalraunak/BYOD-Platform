/**
 * 
 */
package com.device.service.rest.representation;

/**
 * @author raunak
 *
 */
public class UserServiceAuthenticationResponse {

	private String encUserSessionID;			//Encrypted with App Session ID for the service being accessed
	private String encResponseAuthenticator;	//Encrypted with App Session ID for the service being accessed
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