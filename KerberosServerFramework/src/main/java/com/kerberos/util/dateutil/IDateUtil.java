package com.kerberos.util.dateutil;

import java.util.Date;

public interface IDateUtil {

	String createAuthenticator();
	Date createResponseAuthenticator(Date requestAuthenticator);
	Date generateDateFromString(String dateString);
	String generateStringFromDate(Date date);
	Date generateDateWithDelay(int delayInHour);
	boolean validateAuthenticator(Date authenticator);

}
