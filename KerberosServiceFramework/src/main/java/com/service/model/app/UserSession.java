/**
 * 
 */
package com.service.model.app;

import java.util.Calendar;
import java.util.Date;



/**
 * @author raunak
 *
 */
public class UserSession extends ClientSession{


	public UserSession() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, 2);
		expiryTime = calendar.getTime();
	}
	
	
}