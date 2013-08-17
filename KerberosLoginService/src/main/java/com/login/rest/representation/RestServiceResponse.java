/**
 * 
 */
package com.login.rest.representation;

/**
 * @author raunak
 *
 */
public abstract class RestServiceResponse {

	protected String encResponseAuthenticator;

	/**
	 * @return 
	 * <code>String</code> Encrypted Response Authenticator encrypted with <strong>Kerberos Service Session ID</strong> for
	 * <code>AppAuthenticationResponse</code> and <strong>App Session ID</code> for other resposnes except <code>UserAccessServiceResponse</code>
	 * which is encrypted with <strong>User Session ID</strong>
	 */
	public String getEncResponseAuthenticator() {
		return encResponseAuthenticator;
	}

	/**
	 * @param 
	 * <code>String</code> Encrypted Response Authenticator encrypted with <strong>Kerberos Service Session ID</strong> for
	 * <code>AppAuthenticationResponse</code> and <strong>App Session ID</code> for other resposnes except <code>UserAccessServiceResponse</code>
	 * which is encrypted with <strong>User Session ID</strong>
	 */
	public void setEncResponseAuthenticator(String encResponseAuthenticator) {
		this.encResponseAuthenticator = encResponseAuthenticator;
	}
	
	
}
