/**
 * 
 */
package com.kerberos.device.rest.representation.kerberos;

/**
 * @author raunak
 *
 */
public class AuthenticationRequest {

	private String loginName;
	private boolean isApplication;
	
	public AuthenticationRequest() {}
	
	/**
	 * @return the loginName
	 */
	public String getLoginName() {
		return loginName;
	}
	/**
	 * @param loginName the loginName to set
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	/**
	 * @return the isApplication
	 */
	public boolean getIsApplication() {
		return isApplication;
	}
	/**
	 * @param isApplication the isApplication to set
	 */
	public void setIsApplication(boolean isApplication) {
		this.isApplication = isApplication;
	}
}
