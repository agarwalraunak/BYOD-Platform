/**
 * 
 */
package com.service.model.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.management.InvalidAttributeValueException;

import com.service.model.Session;

/**
 * @author raunak
 *
 */
public class ServiceSession extends Session {
	
	private List<Date> authenticators;

	public ServiceSession() {
		authenticators = new LinkedList<>();
	}

	/**
	 * @return the authenticators
	 */
	public List<Date> getAuthenticators() {
		return authenticators;
	}
	

	/**
	 * @return
	 */
	public Date createAuthenticator(){
		
		Date authenticator = null;
		Date lastAuthenticator = null;
		if (authenticators.size() > 0){
			lastAuthenticator = authenticators.get(authenticators.size()-1);
			Calendar c = Calendar.getInstance();
			c.setTime(lastAuthenticator);
			c.add(Calendar.MINUTE, 1);
			authenticator = c.getTime();
		}
		else{
			Date date = new Date();
			DateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
			String authenticatorStr = formatter.format(date);
			try {
				authenticator = formatter.parse(authenticatorStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return authenticator;
	}

	/**
	 * @param authenticator
	 * @throws InvalidAttributeValueException
	 */
	public void addAuthenticator(Date authenticator) {
		
		if(authenticator == null){
			return;
		}
		authenticators.add(authenticator);
	}
	
	/**
	 * @param authenticator
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	public boolean validateAuthenticator(Date authenticator) throws InvalidAttributeValueException{
		
		if(authenticator == null){
			return false;
		}
		
		Date lastAuthenticator;
		if (authenticators.size() > 0)
			lastAuthenticator = authenticators.get(authenticators.size()-1);
		else
			return true;
		
		if (authenticator.getTime() - lastAuthenticator.getTime() != 60000) {
			return false;
		}
		return true;
	}

}
