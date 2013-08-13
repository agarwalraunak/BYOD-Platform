/**
 * 
 */
package com.service.model.app;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.management.InvalidAttributeValueException;

import com.service.model.Session;


/**
 * @author raunak
 *
 */
public class UserSession extends Session{

	private String username;
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
	public boolean validateAuthenticator(Date authenticator) {
		
		if(authenticator == null){
			return false;
		}
		
		Date lastAuthenticator;
		if (authenticators.size() > 0)
			lastAuthenticator = authenticators.get(authenticators.size()-1);
		else
			return true;
		
		if (((new Date().getTime() - authenticator.getTime()) > 5000 * 60) && authenticator.getTime() - lastAuthenticator.getTime() != 60000) {
			return false;
		}
		return true;
	}
}