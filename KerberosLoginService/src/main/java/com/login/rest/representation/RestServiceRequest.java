/**
 * 
 */
package com.login.rest.representation;

/**
 * @author raunak
 *
 */
public abstract class RestServiceRequest {

	protected String encAuthenticator;

	/**
	 * @return <code>String</code>
	 * encAuthenticator encrytped with <strong>Kerberos Service Session ID</strong> for 
	 * <code>AppAuthenticationRequest</code> and <strong>App Session ID</strong> for every
	 * othre request 
	 */
	public String getEncAuthenticator() {
		return encAuthenticator;
	}

	/**
	 * @param encAuthenticator the encAuthenticator to set
	 * encrytped with <strong>Kerberos Service Session ID</strong> for 
	 * <code>AppAuthenticationRequest</code> and <strong>App Session ID</strong> for every
	 * othre request
	 */
	public void setEncAuthenticator(String encAuthenticator) {
		this.encAuthenticator = encAuthenticator;
	}
	
}
