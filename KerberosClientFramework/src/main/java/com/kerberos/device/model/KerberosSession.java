/**
 * 
 */
package com.kerberos.device.model;

import java.util.Date;

/**
 * @author raunak
 *
 */
public abstract class KerberosSession {
	
	private String sessionID;
	private TGT tgt;
	private Date created;
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
}
