/**
 * 
 */
package com.kerberos.service.rest.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kerberos.service.config.applicationdetailservice.ApplicationDetailService;
import com.kerberos.service.models.KerberosSessionManager;
import com.kerberos.service.rest.api.kerberos.IKerberosAuthenticationAPI;
import com.kerberos.service.rest.api.kerberos.KerberosAuthenticationAPIImpl.AuthenticationResponseAttributes;
import com.kerberos.service.util.connectionmanager.IConnectionManager;

/**
 * @author raunak
 *
 */
public class KerberosAuthenticationClient {
	
	private static Logger log = Logger.getLogger(KerberosAuthenticationClient.class);
	
	private static final String AUTHENTICATION_URL = "http://localhost:8080/kerberos/apple/kdc/authenticate/app/";
	private @Autowired IKerberosAuthenticationAPI iKerberosAuthenticationAPI;
	private @Autowired ApplicationDetailService applicationDetailService;
	private @Autowired IConnectionManager iConnectionManager;
	private @Autowired KerberosSessionManager kerberosSessionManager;
	
	/**
	 * @return boolean true if application was authenticated successfully
	 * @throws InvalidAttributeValueException
	 */
	public boolean kerberosAuthentication() throws InvalidAttributeValueException{

		log.debug("Entering authenticateApp method");
		
		String appLoginName = applicationDetailService.getAppLoginName();
		String password = applicationDetailService.getAppPassword();
		
		Map<AuthenticationResponseAttributes, String> responseAttributes = null;
		try {
			responseAttributes = iKerberosAuthenticationAPI.authenticate(AUTHENTICATION_URL, appLoginName, password, true);
		} catch (NoSuchAlgorithmException | IOException e) {
			log.error("Authentication of application failed. Detailed exception attached below:\n"+e.getMessage());
			e.printStackTrace();
			return false;
		}
		
		if (responseAttributes == null){
			return false;
		}
		
		String sessionKey = responseAttributes.get(AuthenticationResponseAttributes.SESSION_KEY);
		String TGTPacket = responseAttributes.get(AuthenticationResponseAttributes.TGT_PACKET);
		
		kerberosSessionManager.createKerberosAppSession(sessionKey, TGTPacket);
		
		log.debug("Returning from authenticateApp method");
		
		return true;
	}
	
}
