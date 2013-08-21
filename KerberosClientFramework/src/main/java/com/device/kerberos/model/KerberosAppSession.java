/**
 * 
 */
package com.device.kerberos.model;

import java.util.Date;
import java.util.Iterator;

import com.device.service.model.AppSession;
import com.device.service.model.UserSession;


/**
 * This class models the Session created by <strong>Kerberos</strong> after authentication
 * 
 * @author raunak
 *
 */
public class KerberosAppSession {
	
	private String sessionID;
	/**
	 * <code>TGT</code> for which the Kerberos App Session is created
	 */
	private TGT tgt;
	private Date created;
	private boolean isActive;
	
	public KerberosAppSession() {
		created = new Date();
	}
	
	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}


	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}


	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}
	/**
	 * @param sessionID the sessionID to set
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	/**
	 * @return the tgt
	 */
	public TGT getTgt() {
		return tgt;
	}
	/**
	 * @param tgt the tgt to set
	 */
	public void setTgt(TGT tgt) {
		this.tgt = tgt;
	}
	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}
	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
	
	/**
	 * 
	 * @param <code>AppSession</code> Deactivates the AppSession by setting the Active Flag to false
	 */
	public void deactiveAppSession(AppSession appSession){
		
		Iterator<String> iterator = tgt.getServiceTickets().keySet().iterator();
		ServiceTicket ticket = null;
		while(iterator.hasNext()){
			ticket = tgt.getServiceTickets().get(iterator.next());
			if (ticket.getActiveAppSession().getSessionID().equals(appSession.getSessionID())){
				ticket.getActiveAppSession().setActive(false);
			}
		}
	}
	
	/**
	 * @param <code>UserSession</code> deactivates the passed in User Session
	 */
	public void deactivateUserSession(UserSession userSession){
		Iterator<String> iterator = tgt.getServiceTickets().keySet().iterator();
		ServiceTicket ticket = null;
		while(iterator.hasNext()){
			ticket = tgt.getServiceTickets().get(iterator.next());
			UserSession session = ticket.getActiveAppSession().findActiveUserServiceSessionByUsername(userSession.getUsername());
			if (session != null && session.getUserSessionID().equals(userSession.getUserSessionID())){
				session.setActive(false);
				
			}
		}
	}
}
