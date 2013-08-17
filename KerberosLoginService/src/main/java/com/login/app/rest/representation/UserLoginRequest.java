package com.login.app.rest.representation;

import com.login.rest.representation.AppRestServiceRequest;

/**
 * @author raunak
 *
 */
public class UserLoginRequest extends AppRestServiceRequest{
	
	private String encUsername;			
	private String encPassword;			

	/**
	 * @return 
	 * <code>String</code> encUsername encrypted using <strong>App Session ID</strong>
	 */
	public String getEncUsername() {
		return encUsername;
	}
	/**
	 * @param 
	 * <code>String</code> encUsername encrypted using <strong>App Session ID</strong>
	 */
	public void setEncUsername(String encUsername) {
		this.encUsername = encUsername;
	}
	/**
	 * @return
	 * <code>String</code> encPassword encrypted using <strong>App Session ID</strong>
	 */
	public String getEncPassword() {
		return encPassword;
	}
	/**
	 * @param
	 * <code>String</code> encPassword encrypted using <strong>App Session ID</strong>
	 */
	public void setEncPassword(String encPassword) {
		this.encPassword = encPassword;
	}
}