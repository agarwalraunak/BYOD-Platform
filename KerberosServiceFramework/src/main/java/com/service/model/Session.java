/**
 * 
 */
package com.service.model;

import java.util.Date;

/**
 * @author raunak
 *
 */
public class Session {
	
	private String sessionID;
	private Date created;
	
	public Session() {
		created = new Date();
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
}
