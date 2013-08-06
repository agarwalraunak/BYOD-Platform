package com.kerberos.rest.representation.device;

public class UserLoginResponse {

	private String encUsername; 						//Encrypted using the AppSessionID
	private String encUserSessionID;					//Encrypted using the AppSessionID
	private String encResponseAuthenticator;	//Encrypted using the AppSessionID
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