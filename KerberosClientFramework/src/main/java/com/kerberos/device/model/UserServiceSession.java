/**
 * 
 */
package com.kerberos.device.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.management.InvalidAttributeValueException;

/**
 * @author raunak
 *
 */
public class UserServiceSession {

	private String userSessionID;
	private String username;
	private Date created;
	private List<Date> authenticators;
	
	public UserServiceSession() {
		created = new Date();
		authenticators = new LinkedList<>();
	}
	/**
	 * @return the userSessionID
	 */
	public String getUserSessionID() {
		return userSessionID;
	}
	/**
	 * @param userSessionID the userSessionID to set
	 */
	public void setUserSessionID(String userSessionID) {
		this.userSessionID = userSessionID;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
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
	public void addAuthenticator(Date authenticator) throws InvalidAttributeValueException{
		
		if(authenticator == null){
			throw new InvalidAttributeValueException("Invalid parameter provided to addAuthenticator");
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
			throw new InvalidAttributeValueException("Invalid parameter provided to addAuthenticator");
		}
		
		Date lastAuthenticator;
		if (authenticators.size() > 0)
			lastAuthenticator = authenticators.get(authenticators.size()-1);
		else
			return false;
		
		if (((new Date().getTime() - authenticator.getTime()) > 5000 * 60) && authenticator.getTime() - lastAuthenticator.getTime() != 60000) {
			return false;
		}
		return true;
	}
	
	
}
