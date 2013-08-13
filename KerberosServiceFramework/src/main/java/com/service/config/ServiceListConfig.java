/**
 * 
 */
package com.service.config;

/**
 * @author raunak
 *
 */
public enum ServiceListConfig {
	
	KEY_SERVER("KEY_SERVER"),
	LOGIN_SERVER("LoginServer");
	
	private String value;
	
	ServiceListConfig(String value){
		this.value = value;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return getValue();
	}
}
