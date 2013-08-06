/**
 * 
 */
package com.kerberos.service.models;

import java.util.Date;

/**
 * @author raunak
 *
 */
public class ServiceTicket {
	
	private String serviceSessionID;
	private String encServiceTicket;	//username,serviceSessionID,serviceTicketExpiryString
	private String serviceName;
	private Date created;
	
	public ServiceTicket(String serviceSessionID, String encServiceTicket, String serviceName){
		created = new Date();
		this.serviceSessionID = serviceSessionID;
		this.encServiceTicket = encServiceTicket;
		this.serviceName = serviceName;
	}
	/**
	 * @return the serviceSessionID
	 */
	public String getServiceSessionID() {
		return serviceSessionID;
	}

	/**
	 * @return the encServiceTicket
	 */
	public String getEncServiceTicket() {
		return encServiceTicket;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}	
}
