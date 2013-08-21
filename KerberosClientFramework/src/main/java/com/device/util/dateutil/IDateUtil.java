package com.device.util.dateutil;

import java.util.Date;

public interface IDateUtil {

	/**
	 * Creates a <strong>Authenticator</strong> and returns a <code>String</code> representaiton of the Authenticator
	 * @return
	 * <code>String</code> Authenticator
	 */
	String createAuthenticator();
	
	/**
	 * Creates a Response Authenticator using the provided Request Authenticator
	 * @param <code>Date</code> requestAuthenticator
	 * @return
	 * <code>Date</code> resposneAuthenticator
	 */
	Date createResponseAuthenticator(Date requestAuthenticator);
	
	/**
	 * Converts the String representation of the Date in to a Date Object
	 * @param <code>String</code> dateString which has to be converted to Date
	 * @return
	 * <code>Date</code> or null if the provided dateString is not valid
	 */
	Date generateDateFromString(String dateString);
	
	/**
	 * Generates a String from the Date Object
	 * @param <code>Date</code> 
	 * @return
	 * <code>String</code> or null if the object is not valid Date Type
	 */
	String generateStringFromDate(Date date);
	
	/**
	 * Generates a Date with the given Delay
	 * @param 
	 * <code>int</code> delayInHour
	 * @return
	 * <code>Date</code>
	 */
	Date generateDateWithDelay(int delayInHour);
	
	/**
	 * @param authenticator
	 * @return
	 */
	boolean validateAuthenticator(Date authenticator);
	
	/**
	 * @param authenticator
	 * @param requestAuthenticator
	 * @return
	 */
	boolean validateAuthenticator(Date authenticator, Date requestAuthenticator);

}
