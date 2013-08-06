/**
 * 
 */
package com.kerberos.service.config.applicationdetailservice;

/**
 * @author raunak
 *
 */
public class ApplicationDetailService {
	
	private String appLoginName;
	private String appPassword;
	
	
	
	/**
	 * @param appLoginName
	 * @param appPassword
	 */
	public ApplicationDetailService(String appLoginName, String appPassword) {
		super();
		this.appLoginName = appLoginName;
		this.appPassword = appPassword;
	}
	
	/**
	 * @return the appLoginName
	 */
	public String getAppLoginName() {
		return appLoginName;
	}


	/**
	 * @return the appPassword
	 */
	public String getAppPassword() {
		return appPassword;
	}
	
}
