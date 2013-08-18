package com.device.service.rest.representation;

import java.util.Map;

public class UserAccessServiceRequest {
	
	private String appID;					//App Username
	private String encAuthenticator; 	//Encrypted using App Session ID
	private String encAppSessionID;		//Encrypted using Kerberos App Service Session ID
	private String encUserSessionID;		//Encrypted using App Session ID
	private Map<String, String> data; //Encrypted using User Session ID
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
	 * @return the data
	 */
	public Map<String, String> getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	
	
	

}
