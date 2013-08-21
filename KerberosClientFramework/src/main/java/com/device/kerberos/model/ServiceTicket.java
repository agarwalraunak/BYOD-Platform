/**
 * 
 */
package com.device.kerberos.model;

import java.util.Date;

import com.device.service.model.AppSession;

/**
 * This model stores the <strong>Kerberos Service Ticket</strong> 
 * 
 * @author raunak
 *
 */
public class ServiceTicket {
	
	private String serviceSessionID;
	/**
	 * Contains of the service ticket are "username,serviceSessionID,serviceTicketExpiryString"
	 * The ticket is encrypted with the <code>SecretKey</code> of the Service for which the ticket
	 * was issued
	 */
	private String encServiceTicket;
	/**
	 * Service UID as given in the Directory
	 */
	private String serviceName;
	/**
	 * <strong>App Session</strong> created after the <code>ServiceTicket</code> authentication by the 
	 * <strong>Service</strong> 
	 */
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
	
	public AppSession getAppSession(){
		return appSession;
	}
	
	/**
	 * @return 
	 * <code>AppSession</code> or null if the AppSession has expired
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
	
	/**
	 * @param <code>String</code> Kerberos App Session ID
	 * @param <code>Date</code> expiry time of the AppSession
	 * @return
	 * <code>AppSession</code>
	 */
	public AppSession createAppServiceSession(String appSessionID, Date expiryTime){
		
		appSession = new AppSession();
		appSession.setSessionID(appSessionID);
		appSession.setExpiryTime(expiryTime);
		return appSession;
	}
	
	
}
