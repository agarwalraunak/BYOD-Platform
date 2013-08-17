/**
 * 
 */
package com.login.service.rest.representation;

import java.util.Map;

import com.login.rest.representation.AppRestServiceRequest;

/**
 * @author raunak
 *
 */
public class AppAccessServiceRequest extends AppRestServiceRequest{

	private Map<String, String> data; 		
	/**
	 * @return
	 * <code>Map<String, String></code> the data encrypted with using App Session ID
	 */
	public Map<String, String> getData() {
		return data;
	}
	/**
	 * @param 
	 * <code>Map<String, String></code> the data encrypted with using App Session ID
	 */
	public void setData(Map<String, String> data) {
		this.data = data;
	}
}