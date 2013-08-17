package com.login.rest.representation;

/**
 * @author raunak
 *
 */
public abstract class AppRestServiceRequest extends RestServiceRequest{
	
	protected String appID;
	protected String encAppSessionID;
	/**
	 * @return the appID
	 * <code>String</code> Plain Text App Login Name
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
	 * @return the encAppSessionID
	 * <code>String</code> Encrypted with <strong>Kerberos Service Session ID</strong>
	 */
	public String getEncAppSessionID() {
		return encAppSessionID;
	}
	/**
	 * @param encAppSessionID the encAppSessionID to set
	 * <code>String</code> Encrypted with <strong>Kerberos Service Session ID</strong>
	 */
	public void setEncAppSessionID(String encAppSessionID) {
		this.encAppSessionID = encAppSessionID;
	}
	
	

}
