package com.login.app.rest.representation;

import com.login.rest.representation.KerberosRequestRepresentation;

public class AppAuthenticationRequest extends KerberosRequestRepresentation {

	private String serviceTicketPacket;
	private String encAuthenticator;			//Encrypted with Kerberos Service Session ID
	/**
	 * @return the serviceTicketPacket
	 */
	public String getServiceTicketPacket() {
		return serviceTicketPacket;
	}
	/**
	 * @param serviceTicketPacket the serviceTicketPacket to set
	 */
	public void setServiceTicketPacket(String serviceTicketPacket) {
		this.serviceTicketPacket = serviceTicketPacket;
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
