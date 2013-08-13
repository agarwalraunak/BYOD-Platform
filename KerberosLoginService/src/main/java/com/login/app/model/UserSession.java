/**
 * 
 */
package com.login.app.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.management.InvalidAttributeValueException;


/**
 * @author raunak
 *
 */
public class UserSession {

	private String username;
	private String userSessionID;
	private List<Date> authenticators;
	
	public UserSession(){
		authenticators = new LinkedList<>();
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