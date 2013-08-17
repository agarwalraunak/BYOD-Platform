/**
 * 
 */
package com.login.service.rest.representation;

import java.util.Map;

import com.login.rest.representation.RestServiceResponse;

/**
 * @author raunak
 *
 */
public class UserAccessServiceResponse extends RestServiceResponse {
	
	private Map<String, String> data;

	/**
	 * @return
	 * <code>Map<String, String></code> response data encrypted with '
	 * <strong>User Session ID</strong>
	 */
	public Map<String, String> getData() {
		return data;
	}
	/**
	 * @param 
	 * <code>Map<String, String></code> response data encrypted with '
	 * <strong>User Session ID</strong>
	 */
	public void setData(Map<String, String> data) {
		this.data = data;
	}
}
