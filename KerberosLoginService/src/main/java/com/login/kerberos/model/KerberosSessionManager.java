/**
 * 
 */
package com.login.kerberos.model;

import javax.management.InvalidAttributeValueException;

import org.springframework.stereotype.Component;


/**
 * @author raunak
 *
 */
@Component
public class KerberosSessionManager {

	private KerberosAppSession appSession;

	public KerberosSessionManager() {
	}

	/**
	 * @return the appSession
	 */
	public KerberosAppSession getAppSession() {
		return appSession;
	}

	public KerberosAppSession createKerberosAppSession(String sessionID, String TGTPacket) throws InvalidAttributeValueException{
		
		if (sessionID == null || sessionID.isEmpty() || TGTPacket == null || TGTPacket.isEmpty()){
			throw new InvalidAttributeValueException("Invalid input parameter to createKerberosAppSession");
		}
		
		appSession = new KerberosAppSession();
		appSession.setSessionID(sessionID);
		appSession.setTgt(new TGT(TGTPacket));
		
		return appSession;
	}	
}