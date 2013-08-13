package com.login.app.rest.representation;

import com.login.rest.representation.KerberosRequestRepresentation;

public class UserLoginRequest extends KerberosRequestRepresentation{
	
	private String encUsername;			//Encrypted using app session id
	private String encPassword;			//Encrypted using app session id
	private String encAppSessionID;		//Encrypted using service session id
	private String appID;					//App username
	private String encAuthenticator;		//Encryped using app session id
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
	 * @return the encPassword
	 */
	public String getEncPassword() {
		return encPassword;
	}
	/**
	 * @param encPassword the encPassword to set
	 */
	public void setEncPassword(String encPassword) {
		this.encPassword = encPassword;
	}
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