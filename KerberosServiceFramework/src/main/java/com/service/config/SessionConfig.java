/**
 * 
 */
package com.service.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

/**
 * @author raunak
 *
 */
public class SessionConfig {
	
	private static final int APP_SESSION_TIME_OUT;
	private static final int USER_SESSION_TIME_OUT;

	static{
		Properties properties = new Properties();
		InputStream inputStream = SessionConfig.class.getClassLoader().getResourceAsStream("SessionConfiguration.properties");
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Configuration Error: Session Configuration failed!");
		}
		APP_SESSION_TIME_OUT = Integer.parseInt((String)properties.get("APP_SESSION_TIME_OUT"));
		USER_SESSION_TIME_OUT = Integer.parseInt((String)properties.get("USER_SESSION_TIME_OUT"));
	}

	/**
	 * @return the appSessionTimeOut
	 */
	public static int getAppSessionTimeOut() {
		return APP_SESSION_TIME_OUT;
	}

	/**
	 * @return the userSessionTimeOut
	 */
	public static int getUserSessionTimeOut() {
		return USER_SESSION_TIME_OUT;
	}
	
	public static Date getAppSessionExpiryTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, APP_SESSION_TIME_OUT);
		return calendar.getTime();
	}
	
	public static Date getUserSessionExpiryTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, USER_SESSION_TIME_OUT);
		return calendar.getTime();
	}
}
