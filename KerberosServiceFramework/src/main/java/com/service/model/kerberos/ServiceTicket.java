/**
 * 
 */
package com.service.model.kerberos;

import java.util.Date;

import com.service.model.service.ServiceSession;

/**
 * @author raunak
 *
 */
public class ServiceTicket {
	
	private String serviceSessionID;
	private String encServiceTicket;	//username,serviceSessionID,serviceTicketExpiryString
	private String serviceName;
	private ServiceSession serviceSession;
	private Date created;
	private Date expiryTime;
	
	public ServiceTicket(String serviceSessionID, String encServiceTicket, String serviceName, Date expiryTime){
		created = new Date();
		this.serviceSessionID = serviceSessionID;
		this.encServiceTicket = encServiceTicket;
		this.serviceName = serviceName;
		this.expiryTime = expiryTime;
	}
	
	/**
	 * @return the serviceSession
	 */
	public ServiceSession getServiceSession() {
		return serviceSession;
	}


	/**
	 * @return the expiryTime
	 */
	public Date getExpiryTime() {
		return expiryTime;
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
	
	public ServiceSession createServiceSession(String sessionID){
		
		serviceSession = new ServiceSession();
		serviceSession.setSessionID(sessionID);
		return serviceSession;
	}
	
	
}
