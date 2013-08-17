package com.login.app.rest.representation;

import com.login.rest.representation.RestServiceRequest;

/**
 * @author raunak
 *
 */
public class AppAuthenticationRequest extends RestServiceRequest {

	private String serviceTicketPacket;
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
}
