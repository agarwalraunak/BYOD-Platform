/**
 * 
 */
package com.service.util.dateutil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * @author raunak
 *
 */
@Component
public class DateUtilImpl implements IDateUtil {
	
	/**
	 * @param delayInHour
	 * @return
	 */
	@Override
	public Date generateDateWithDelay(int delayInHour){
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR_OF_DAY, delayInHour);
		Date date = calendar.getTime();
		return date;
	}
	
	/**
	 * @param date
	 * @return
	 */
	@Override
	public String generateStringFromDate(Date date){
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
		return formatter.format(date);
	}
	
	/**
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	@Override
	public Date generateDateFromString(String dateString){
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
		try {
			return formatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Validates the Authenticator i.e. Authenticator should not be older than 5minutes from current timestamp
	 * @param authenticator
	 * @return
	 */
	@Override
	public boolean validateAuthenticator(Date authenticator){
		if (((new Date().getTime() - authenticator.getTime()) > 5000 * 60)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Validates the Authenticator i.e. Authenticator should not be older than 5minutes from current timestamp
	 * @param authenticator
	 * @return
	 */
	@Override
	public boolean validateAuthenticator(Date authenticator, Date requestAuthenticator){
		if (((new Date().getTime() - authenticator.getTime()) > 5000 * 60) && authenticator.getTime() - requestAuthenticator.getTime() != 60000) {
			return false;
		}
		return true;
	}

	
	/**
	 * Generates and returns an Authenticator (i.e. timestamp String)
	 * @param SecretKey key
	 * @return
	 */
	@Override
	public String createAuthenticator(){

		Date date = new Date();
		DateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
		String authenticator = formatter.format(date);
		
		return authenticator;
	}
	
	@Override
	public Date createResponseAuthenticator(Date requestAuthenticator){
		
		Calendar c = Calendar.getInstance();
		c.setTime(requestAuthenticator);
		c.add(Calendar.MINUTE, 1);
		Date responseAuthenticator = c.getTime();
		
		return responseAuthenticator;
	}
}
