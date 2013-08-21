package com.service.config.applicationdetailservice;

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
	 * @return Configured App Login Name
	 */
	public String getAppLoginName() {
		return appLoginName;
	}


	/**
	 * @return Configured App Password
	 */
	public String getAppPassword() {
		return appPassword;
	}
	
}
