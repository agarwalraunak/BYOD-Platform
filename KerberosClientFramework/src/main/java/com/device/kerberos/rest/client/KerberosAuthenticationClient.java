/**
 * 
 */
package com.device.kerberos.rest.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.device.applicationdetailservice.ApplicationDetailService;
import com.device.config.KerberosURLConfig;
import com.device.kerberos.model.KerberosSessionManager;
import com.device.kerberos.rest.api.IKerberosAppAuthenticationAPI;
import com.device.kerberos.rest.api.KerberosAppAuthenticationAPIImpl.AuthenticationResponseAttributes;
import com.device.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */
@Component
public class KerberosAuthenticationClient {
	
	private static Logger log = Logger.getLogger(KerberosAuthenticationClient.class);
	
	private @Autowired IKerberosAppAuthenticationAPI iAuthenticationAPI;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired ApplicationDetailService applicationDetailService;
	private @Autowired KerberosSessionManager kerberosSessionManager;
	private @Autowired KerberosURLConfig kerberosURLConfig;
	
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
			responseAttributes = iAuthenticationAPI.authenticate(kerberosURLConfig.getKERBEROS_APP_AUTHENTICATION_URL(), appLoginName, password, true);
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
