package com.login.rest.representation;

/**
 * @author raunak
 *
 */
public abstract class UserRestServiceRequest extends AppRestServiceRequest{

	protected String encUserSessionID;

	/**
	 * @return 
	 * <code>String</code> Encrypted using <strong>AppSessionID</strong>
	 */
	public String getEncUserSessionID() {
		return encUserSessionID;
	}

	/**
	 * @param 
	 * <code>String</code> encUserSessionID Encrypted using <strong>AppSessionID</strong>
	 */
	public void setEncUserSessionID(String encUserSessionID) {
		this.encUserSessionID = encUserSessionID;
	}
	
	
}
