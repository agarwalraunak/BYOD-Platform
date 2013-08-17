/**
 * 
 */
package com.login.service.rest.representation;

import com.login.rest.representation.RestServiceResponse;

/**
 * @author raunak
 *
 */
public class ServiceValidateUserAuthenticationResponse extends RestServiceResponse{
	
	private String encIsAuthenticated;
	/**
	 * @return <code>String</code> isAuthenticated encrytped with <strong>App Session ID</strong>
	 */
	public String getEncIsAuthenticated() {
		return encIsAuthenticated;
	}
	/**
	 * @param <code>String</code> isAuthenticated encrytped with <strong>App Session ID</strong>
	 */
	public void setEncIsAuthenticated(String encIsAuthenticated) {
		this.encIsAuthenticated = encIsAuthenticated;
	}
}
