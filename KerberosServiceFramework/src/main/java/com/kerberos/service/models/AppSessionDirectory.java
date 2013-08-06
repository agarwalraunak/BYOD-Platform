package com.kerberos.service.models;

import java.util.HashMap;
import java.util.Map;

import javax.management.InvalidAttributeValueException;

import org.springframework.beans.factory.annotation.Autowired;

import com.kerberos.service.util.hashing.IHashUtil;

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
	
	public AppSession findAppSessionByAppID(String appID) throws InvalidAttributeValueException{
		
		if(appID == null){
			throw new InvalidAttributeValueException("Invalid attribute value for findAppSessionByAppID");
		}
		
		return appSessionDirectory.get(appID);
	}
}
