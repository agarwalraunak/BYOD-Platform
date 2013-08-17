/**
 * 
 */
package com.device.kerberos.rest.client;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.device.applicationdetailservice.ApplicationDetailService;
import com.device.config.KerberosURLConfig;
import com.device.exception.ApplicationDetailServiceUninitializedException;
import com.device.exception.ResponseDecryptionException;
import com.device.exception.RestClientException;
import com.device.kerberos.model.KerberosSessionManager;
import com.device.kerberos.rest.api.IKerberosAuthenticationAPI;
import com.device.kerberos.rest.api.KerberosAuthenticationAPIImpl.AuthenticationResponseAttributes;
import com.device.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */

@Component
public class KerberosAuthenticationClientImpl implements IKerberosAuthenticationClient{
	
	private static Logger log = Logger.getLogger(KerberosAuthenticationClientImpl.class);
	
	private @Autowired IKerberosAuthenticationAPI iAuthenticationAPI;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired ApplicationDetailService applicationDetailService;
	private @Autowired KerberosSessionManager kerberosSessionManager;
	private @Autowired KerberosURLConfig kerberosURLConfig;
	
	@Override
	public boolean kerberosAuthentication() throws IOException, RestClientException, ResponseDecryptionException, ApplicationDetailServiceUninitializedException{

		log.debug("Entering authenticateApp method");
		
		if (kerberosSessionManager.getKerberosAppSession() != null){
			return true;
		}
		
		String appLoginName = applicationDetailService.getAppLoginName();
		String password = applicationDetailService.getAppPassword();
		
		if (!iEncryptionUtil.validateDecryptedAttributes(appLoginName, password)) {
			log.error("Invalid app credentials. AppLoginName and Password can not be empty or null");
			throw new ApplicationDetailServiceUninitializedException();
		}
		
		Map<AuthenticationResponseAttributes, String> responseAttributes = iAuthenticationAPI.authenticate(kerberosURLConfig.getKERBEROS_APP_AUTHENTICATION_URL(), appLoginName, password);
		
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
