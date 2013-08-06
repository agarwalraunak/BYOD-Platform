/**
 * 
 */
package com.kerberos.ActiveDirectory;

/**
 * @author HIE Prototype Dev Team
 * 
 */
public class EntryDetails {
	private String commonName;
	private String surName;
	private String uid;
	private String userPassword;

	/**
	 * @return the commonName
	 */
	public String getCommonName() {
		return commonName;
	}

	/**
	 * @param commonName
	 *            the commonName to set
	 */
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	/**
	 * @return the surName
	 */
	public String getSurName() {
		return surName;
	}

	/**
	 * @param surName
	 *            the surName to set
	 */
	public void setSurName(String surName) {
		this.surName = surName;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the userPassword
	 */
	public String getUserPassword() {
		return userPassword;
	}

	/**
	 * @param userPassword
	 *            the userPassword to set
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

}
