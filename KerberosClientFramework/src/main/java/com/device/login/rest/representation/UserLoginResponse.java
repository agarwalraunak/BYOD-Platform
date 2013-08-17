package com.device.login.rest.representation;

import com.device.rest.representation.KerberosRequestRepresentation;

public class UserLoginResponse extends KerberosRequestRepresentation{

	private String encUsername; 						//Encrypted using the AppSessionID
	private String encUserSessionID;					//Encrypted using the AppSessionID
	private String encResponseAuthenticator;	//Encrypted using the AppSessionID
	private String encExpiryTime;
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