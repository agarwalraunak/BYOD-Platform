/**
 * 
 */
package com.login.app.rest.representation;

import com.login.rest.representation.KerberosRequestRepresentation;

/**
 * @author raunak
 *
 */
public class AppAuthenticationResponse extends KerberosRequestRepresentation {
	
	private String encAppSessionID;					//Encrypted with Service Key
	private String encResponseAuthenticator; 	//Encrypted using Kerberos Service Session ID
	/**
	 * @return the encAppSessionID
	 */
	public String getEncAppSessionID() {
		return encAppSessionID;
	}
	/**
	 * @param encAppSessionID the encAppSessionID to set
	 */
	public void setEncAppSessionID(String encAppSessionID) {
		this.encAppSessionID = encAppSessionID;
	}
	/**
	 * @return the encResponseAuthenticator
	 */
	public String getEncResponseAuthenticator() {
		return encResponseAuthenticator;
	}
	/**
	 * @param encResponseAuthenticator the encResponseAuthenticator to set
	 */
	public void setEncResponseAuthenticator(String encResponseAuthenticator) {
		this.encResponseAuthenticator = encResponseAuthenticator;
	}
}
