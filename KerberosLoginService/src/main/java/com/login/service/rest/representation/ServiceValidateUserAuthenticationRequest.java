/**
 * 
 */
package com.login.service.rest.representation;

import com.login.rest.representation.AppRestServiceRequest;

/**
 * @author raunak
 *
 */
public class ServiceValidateUserAuthenticationRequest extends AppRestServiceRequest {
	
	private String encUserLoginSessionID;
	/**
	 * @return
	 * <code>String</code> encUserLoginSessionID encrypted with App Session ID
	 */
	public String getEncUserLoginSessionID() {
		return encUserLoginSessionID;
	}
	/**
	 * @param 
	 * <code>String</code> encUserLoginSessionID encrypted with App Session ID
	 */
	public void setEncUserLoginSessionID(String encUserLoginSessionID) {
		this.encUserLoginSessionID = encUserLoginSessionID;
	}
}
