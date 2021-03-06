/**
 * 
 */
package com.service.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.model.app.AppSession;
import com.service.model.kerberos.KerberosAppSession;
import com.service.model.kerberos.TGT;
import com.service.util.hashing.IHashUtil;

/**
 * @author raunak
 *
 */
@Component
public class SessionDirectory {
	
	private KerberosAppSession kerberosAppSession;
	private Map<String, AppSession> appSessionDirectory;		//Key is the appLoginName
	private @Autowired IHashUtil iHashUtil;
	
	public SessionDirectory() {
		appSessionDirectory = new HashMap<>();
	}
	
	/**
	 * @return the appSession
	 */
	public KerberosAppSession getKerberosAppSession() {
		return kerberosAppSession;
	}

	
	/**
	 * @param kerberosAppSession the kerberosAppSession to set
	 */
	public void setKerberosAppSession(KerberosAppSession kerberosAppSession) {
		this.kerberosAppSession = kerberosAppSession;
	}

	/**
	 * @return the appSessionDirectory
	 */
	public Map<String, AppSession> getAppSessionDirectory() {
		return appSessionDirectory;
	}

	/**
	 * @param sessionID
	 * @param TGTPacket
	 * @return
	 */
	public KerberosAppSession createKerberosAppSession(String sessionID, String TGTPacket) {
		
		if (sessionID == null || sessionID.isEmpty() || TGTPacket == null || TGTPacket.isEmpty()){
			return null;
		}
		
		kerberosAppSession = new KerberosAppSession();
		kerberosAppSession.setSessionID(sessionID);
		kerberosAppSession.setTgt(new TGT(TGTPacket));
		
		return kerberosAppSession;
	}	

	/**
	 * @param serviceSessionID
	 * @param username
	 * @return AppSession or null if the AppSession does not exist or input parameters are invalid
	 */
	public AppSession createAppSession(String serviceSessionID, String username, String clientIP) {
		
		if (serviceSessionID == null || serviceSessionID.isEmpty() || username == null || username.isEmpty() || clientIP == null || clientIP.isEmpty()){
			return null;
		}
		
		AppSession appSession = new AppSession();
		appSession.setLoginName(username);
		appSession.setKerberosServiceSessionID(serviceSessionID);
		appSession.setSessionID(iHashUtil.getSessionKey());
		appSession.setClientIP(clientIP);
		
		appSessionDirectory.put(username, appSession);
		
		return appSession;
	}
	
	/**
	 * @param appID
	 * @return
	 */
	public AppSession findActiveAppSessionByAppID(String appID){
		
		if(appID == null){
			return null;
		}
		
		AppSession appSession = appSessionDirectory.get(appID);
		
		if (appSession != null){
			//Check if the appSession active flag is true and if it has expired set Active flag to false
			if (appSession.isActive() && appSession.getExpiryTime().before(new Date())){
				appSession.setActive(false);
			}
			if (appSession.isActive())
				return appSession;
		}
			
		return null;
	}

}
