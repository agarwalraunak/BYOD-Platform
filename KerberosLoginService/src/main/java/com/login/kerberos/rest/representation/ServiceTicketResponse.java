/**
 * 
 */
package com.login.kerberos.rest.representation;


/**
 * @author raunak
 *
 */
public class ServiceTicketResponse  {

	private String encServiceTicket;
	private String encServiceSessionID;
	private String encServiceName;
	private String encAuthenticator;
	private String encExpiryTime;
	
	/**
	 * @return the encServiceTicket
	 */
	public String getEncServiceTicket() {
		return encServiceTicket;
	}
	/**
	 * @param encServiceTicket the encServiceTicket to set
	 */
	public void setEncServiceTicket(String encServiceTicket) {
		this.encServiceTicket = encServiceTicket;
	}
	/**
	 * @return the encServiceSessionID
	 */
	public String getEncServiceSessionID() {
		return encServiceSessionID;
	}
	/**
	 * @param encServiceSessionID the encServiceSessionID to set
	 */
	public void setEncServiceSessionID(String encServiceSessionID) {
		this.encServiceSessionID = encServiceSessionID;
	}
	/**
	 * @return the encServiceName
	 */
	public String getEncServiceName() {
		return encServiceName;
	}
	/**
	 * @param encServiceName the encServiceName to set
	 */
	public void setEncServiceName(String encServiceName) {
		this.encServiceName = encServiceName;
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
