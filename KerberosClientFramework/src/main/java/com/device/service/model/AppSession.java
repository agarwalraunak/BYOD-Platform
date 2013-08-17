/**
 * 
 */
package com.device.service.model;

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
public class AppSession {

	private String sessionID;
	private Date created;
	private List<UserSession> userSessions;
	private List<Date> authenticators;
	private boolean isActive;
	private Date expiryTime;
	
	public AppSession() {
		isActive = true;
		created = new Date();
		userSessions = new ArrayList<>();
		authenticators = new LinkedList<>();
	}
	
	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
		if (!isActive){
			for (UserSession session : userSessions){
				session.setActive(false);
			}
		}
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
	public List<UserSession> getUserServiceSessions() {
		return userSessions;
	}
	
	/**
	 * @return the expiryTime
	 */
	public Date getExpiryTime() {
		return expiryTime;
	}

	/**
	 * @param expiryTime the expiryTime to set
	 */
	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
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
	 * @param username
	 * @param userSessionID
	 * @return created UserSession else null 
	 * @throws InvalidAttributeValueException
	 */
	public UserSession createUserServiceSession(String username, String userSessionID, Date expiryTime) {
		
		if (username == null || userSessionID == null){
			return null;
		}
		
		UserSession serviceSession = new UserSession();
		serviceSession.setUsername(username);
		serviceSession.setUserSessionID(userSessionID);
		serviceSession.setExpiryTime(expiryTime);
		
		userSessions.add(serviceSession);
		
		return serviceSession;
	}
	
	/**
	 * @param username
	 * @return UserSession related to the username
	 */
	public UserSession findActiveUserServiceSessionByUsername(String username) {
		if (username == null || username.isEmpty()){
			throw new IllegalArgumentException("findUserServiceSessionByUsername");
		}
		
		for(UserSession session : userSessions){
			if (session.getUsername().equals(username) && session.isActive()){
				if (session.getExpiryTime().before(new Date())){
					session.setActive(false);
				}
				else{
					return session;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * @param authenticator
	 * @throws InvalidAttributeValueException
	 */
	public void addAuthenticator(Date authenticator) {
		
		if(authenticator == null){
			throw new IllegalArgumentException("addAuthenticator");
		}
		authenticators.add(authenticator);
	}
	
	/**
	 * @param authenticator
	 * @return
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
		
		if (authenticator.getTime() - lastAuthenticator.getTime() != 60000) {
			return false;
		}
		return true;
	}
	
}
