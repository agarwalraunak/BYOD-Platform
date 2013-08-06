/**
 * 
 */
package com.kerberos.db.model;

/**
 * @author HIE Prototype Dev Team
 *
 */
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ServiceTicket {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private Date expiresOn;
	private boolean isActive;
	private String identifier;			//Service Session Key
	private Date createdOn;
	private String serviceName;
	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="tgt")
	private TGT tgt;
	
	public ServiceTicket(){
		createdOn = new Date();
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
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
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
}
