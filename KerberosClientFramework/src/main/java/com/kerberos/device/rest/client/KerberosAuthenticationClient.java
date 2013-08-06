/**
 * 
 */
package com.kerberos.device.rest.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kerberos.device.applicationdetailservice.ApplicationDetailService;
import com.kerberos.device.model.KerberosSessionManager;
import com.kerberos.device.rest.api.AuthenticationAPIImpl.AuthenticationResponseAttributes;
import com.kerberos.device.rest.api.IAuthenticationAPI;
import com.kerberos.device.util.connectionmanager.IConnectionManager;
import com.kerberos.device.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */
public class KerberosAuthenticationClient {
	
	private static Logger log = Logger.getLogger(KerberosAuthenticationClient.class);
	
	private static final String AUTHENTICATION_URL = "http://localhost:8080/kerberos/apple/kdc/authenticate/app/";
	private @Autowired IAuthenticationAPI iAuthenticationAPI;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired ApplicationDetailService applicationDetailService;
	private @Autowired IConnectionManager iConnectionManager;
	private @Autowired KerberosSessionManager kerberosSessionManager;
	
	/**
	 * @return boolean true if application was authenticated successfully
	 * @throws InvalidAttributeValueException
	 */
	public boolean authenticateApp() throws InvalidAttributeValueException{

		log.debug("Entering authenticateApp method");
		
		String appLoginName = applicationDetailService.getAppLoginName();
		String password = applicationDetailService.getAppPassword();
		
		if (!iEncryptionUtil.validateDecryptedAttributes(appLoginName, password)) {
			log.error("Invalid app credentials. AppLoginName and Password can not be empty or null");
			throw new InvalidAttributeValueException("Invalid app credentials. AppLoginName and Password can not be empty or null");
		}
		
		
		Map<AuthenticationResponseAttributes, String> responseAttributes = null;
		try {
			responseAttributes = iAuthenticationAPI.authenticate(AUTHENTICATION_URL, appLoginName, password, true);
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
