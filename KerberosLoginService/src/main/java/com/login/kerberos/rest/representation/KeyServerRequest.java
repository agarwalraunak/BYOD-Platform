/**
 * 
 */
package com.login.kerberos.rest.representation;




/**
 * @author raunak
 *
 */
public class KeyServerRequest {

	private String keyType;
	private String encServiceTicket;
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
	 * @return the keyType
	 */
	public String getKeyType() {
		return keyType;
	}

	/**
	 * @param keyType the keyType to set
	 */
	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}
	
	
}
