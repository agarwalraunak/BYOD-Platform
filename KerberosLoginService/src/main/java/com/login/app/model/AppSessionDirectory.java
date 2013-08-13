package com.login.app.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.InvalidAttributeValueException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.util.hashing.IHashUtil;

@Component
public class AppSessionDirectory {

	private Map<String, AppSession> appSessionDirectory;		//Key is the appLoginName
	private @Autowired IHashUtil iHashUtil;
	
	public AppSessionDirectory() {
		appSessionDirectory = new HashMap<>();
	}

	/**
	 * @return the appSessionDirectory
	 */
	public Map<String, AppSession> getAppSessionDirectory() {
		return appSessionDirectory;
	}

	/**
	 * @param appSessionDirectory the appSessionDirectory to set
	 */
	public void setAppSessionDirectory(Map<String, AppSession> appSessionDirectory) {
		this.appSessionDirectory = appSessionDirectory;
	}
	
	public AppSession createAppSession(String serviceSessionID, String username) throws InvalidAttributeValueException{
		
		if (serviceSessionID == null || serviceSessionID.isEmpty() || username == null || username.isEmpty()){
			throw new InvalidAttributeValueException("Invalid input parameter provided to createAppSession");
		}
		
		AppSession appSession = new AppSession();
		appSession.setAppID(username);
		appSession.setAppServiceSessionID(serviceSessionID);
		appSession.setAppSessionID(iHashUtil.getSessionKey());
		
		appSessionDirectory.put(username, appSession);
		
		return appSession;
	}
	
	public AppSession findAppSessionByAppID(String appID) {
		
		if(appID == null){
			return null;
		}
		
		return appSessionDirectory.get(appID);
	}
	
	public UserSession findUserSessionBySessionID(String userSessionID){
		Iterator<String> iterator = appSessionDirectory.keySet().iterator();
		AppSession appSession = null;
		while(iterator.hasNext()){
			appSession = appSessionDirectory.get(iterator.next());
			for (UserSession userSession : appSession.getUserSession()){
				if (userSession.getUserSessionID().equals(userSessionID))
					return userSession;
			}
		}
		return null;
	}
}
