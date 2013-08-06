/**
 * 
 */
package com.kerberos.rest.representation.kerberos;

/**
 * @author raunak
 *
 */
public class ServiceTicketRequest {
	
	private String encAppTgtPacket;
	private String encServiceName;
	private String encAuthenticator;
	
	public ServiceTicketRequest() {	}
	
	/**
	 * @param encAppTgtPacket
	 * @param encServiceName
	 * @param encAuthenticator
	 */
	public ServiceTicketRequest(String encAppTgtPacket, String encServiceName,
			String encAuthenticator) {
		super();
		this.encAppTgtPacket = encAppTgtPacket;
		this.encServiceName = encServiceName;
		this.encAuthenticator = encAuthenticator;
	}

	/**
	 * @return the encAppTgtPacket
	 */
	public String getEncAppTgtPacket() {
		return encAppTgtPacket;
	}
	/**
	 * @return the encServiceName
	 */
	public String getEncServiceName() {
		return encServiceName;
	}
	/**
	 * @return the encAuthenticator
	 */
	public String getEncAuthenticator() {
		return encAuthenticator;
	}

	/**
	 * @param encAppTgtPacket the encAppTgtPacket to set
	 */
	public void setEncAppTgtPacket(String encAppTgtPacket) {
		this.encAppTgtPacket = encAppTgtPacket;
	}

	/**
	 * @param encServiceName the encServiceName to set
	 */
	public void setEncServiceName(String encServiceName) {
		this.encServiceName = encServiceName;
	}

	/**
	 * @param encAuthenticator the encAuthenticator to set
	 */
	public void setEncAuthenticator(String encAuthenticator) {
		this.encAuthenticator = encAuthenticator;
	}
	

}