/**
 * 
 */
package com.kerberos.rest.representation.kerberos;

/**
 * @author raunak
 *
 */
public class ServiceTicketResponse {

	private String encServiceTicket;
	private String encServiceSessionID;
	private String encServiceName;
	private String encAuthenticator;
	
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
	
}
