/**
 * 
 */
package com.device.kerberos.model;

import java.util.Date;

import com.device.service.model.AppSession;

/**
 * @author raunak
 *
 */
public class ServiceTicket {
	
	private String serviceSessionID;
	private String encServiceTicket;	//username,serviceSessionID,serviceTicketExpiryString
	private String serviceName;
	private AppSession appSession;
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
	 * @return the appSession
	 */
	public AppSession getActiveAppSession() {
		if (appSession != null && appSession.isActive()){
			if (appSession.getExpiryTime().before(new Date())){
				appSession.setActive(false);
			}
			else{
				return appSession;
			}
		}
		return null;
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
	
	public AppSession createAppServiceSession(String appSessionID, Date expiryTime){
		
		appSession = new AppSession();
		appSession.setSessionID(appSessionID);
		appSession.setExpiryTime(expiryTime);
		return appSession;
	}
	
	
}
