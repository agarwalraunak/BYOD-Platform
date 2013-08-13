/**
 * 
 */
package com.service.kerberos.rest.representation;

import com.service.rest.representation.KerberosRequestRepresentation;

/**
 * @author raunak
 *
 */
public class AuthenticationResponse extends KerberosRequestRepresentation{
	
	private String encTgtPacket;	//TGTPacket = loginName,sessionkey,expiryTimeStamp
	private String encSessionKey;
	private String encLoginName;
	/**
	 * @return the encTgtPacket
	 */
	public String getEncTgtPacket() {
		return encTgtPacket;
	}
	/**
	 * @param encTgtPacket the encTgtPacket to set
	 */
	public void setEncTgtPacket(String encTgtPacket) {
		this.encTgtPacket = encTgtPacket;
	}
	/**
	 * @return the encSessionKey
	 */
	public String getEncSessionKey() {
		return encSessionKey;
	}
	/**
	 * @param encSessionKey the encSessionKey to set
	 */
	public void setEncSessionKey(String encSessionKey) {
		this.encSessionKey = encSessionKey;
	}
	/**
	 * @return the encLoginName
	 */
	public String getEncLoginName() {
		return encLoginName;
	}
	/**
	 * @param encLoginName the encLoginName to set
	 */
	public void setEncLoginName(String encLoginName) {
		this.encLoginName = encLoginName;
	}
}
