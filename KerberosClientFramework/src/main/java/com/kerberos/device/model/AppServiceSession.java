/**
 * 
 */
package com.kerberos.device.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.management.InvalidAttributeValueException;

/**
 * @author raunak
 *
 */
public class AppServiceSession {

	private String sessionID;
	private Date created;
	private List<UserServiceSession> userServiceSessions;
	private List<Date> authenticators;
	
	public AppServiceSession() {
		userServiceSessions = new ArrayList<>();
		authenticators = new LinkedList<>();
	}
	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}
	/**
	 * @param sessionID the sessionID to set
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}
	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
	/**
	 * @return the authenticators
	 */
	public List<Date> getAuthenticators() {
		return authenticators;
	}
	/**
	 * @return the userServiceSessions
	 */
	public List<UserServiceSession> getUserServiceSessions() {
		return userServiceSessions;
	}
	/**
	 * @param username
	 * @param userSessionID
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	public UserServiceSession createUserServiceSession(String username, String userSessionID) throws InvalidAttributeValueException{
		
		if (username == null || userSessionID == null){
			throw new InvalidAttributeValueException("Invalid parameter provided to createUserServiceSession");
		}
		
		UserServiceSession serviceSession = new UserServiceSession();
		serviceSession.setUsername(username);
		serviceSession.setUserSessionID(userSessionID);
		
		userServiceSessions.add(serviceSession);
		
		return serviceSession;
	}
	
	public UserServiceSession findUserServiceSessionByUsername(String username) throws InvalidAttributeValueException {
		if (username == null || username.isEmpty()){
			throw new InvalidAttributeValueException("Invalid input parameter provided to findUserServiceSessionByUsername");
		}
		
		for(UserServiceSession session : userServiceSessions){
			if (session.getUsername().equals(username)){
				return session;
			}
		}
		
		return null;
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
