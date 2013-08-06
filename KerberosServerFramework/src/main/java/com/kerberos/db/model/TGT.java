/**
 * 
 */
package com.kerberos.db.model;

/**
 * @author HIE Prototype Dev Team
 *
 */

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class TGT {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String username;
	private String sessionKey;
	private Date created;
	private Date expiresOn;
	private Boolean isActive;
	
	@OneToMany(mappedBy="tgt", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
	private List<ServiceTicket> serviceTickets;
	
	public TGT(){
		created = new Date();
		isActive = true;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the sessionKey
	 */
	public String getSessionKey() {
		return sessionKey;
	}
	/**
	 * @param sessionKey the sessionKey to set
	 */
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
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
	 * @return the expiresOn
	 */
	public Date getExpiresOn() {
		return expiresOn;
	}
	/**
	 * @param expiresOn the expiresOn to set
	 */
	public void setExpiresOn(Date expiresOn) {
		this.expiresOn = expiresOn;
	}
	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the serviceTickets
	 */
	public List<ServiceTicket> getServiceTickets() {
		return serviceTickets;
	}

	/**
	 * @param serviceTickets the serviceTickets to set
	 */
	public void setServiceTickets(List<ServiceTicket> serviceTickets) {
		this.serviceTickets = serviceTickets;
	}
	
	

}
