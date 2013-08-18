/**
 * 
 */
package com.service.model.app;

import com.service.config.SessionConfig;



/**
 * @author raunak
 *
 */
public class UserSession extends ClientSession{


	public UserSession() {
		expiryTime = SessionConfig.getUserSessionExpiryTime();
	}
	
	
}