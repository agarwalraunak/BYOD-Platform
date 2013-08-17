/**
 * 
 */
package com.login.kerberos.rest.client;


import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.config.KerberosURLConfig;
import com.login.config.applicationdetailservice.ApplicationDetailService;
import com.login.exception.ApplicationDetailServiceUninitializedException;
import com.login.exception.ResponseDecryptionException;
import com.login.exception.RestClientException;
import com.login.kerberos.rest.api.IKerberosAuthenticationAPI;
import com.login.kerberos.rest.api.KerberosAuthenticationAPIImpl.AuthenticationResponseAttributes;
import com.login.model.SessionDirectory;
import com.login.model.kerberos.KerberosAppSession;
import com.login.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */
@Component
public class KerberosAuthenticationClientImpl implements IKerberosAuthenticationClient{
	
	private static Logger log = Logger.getLogger(KerberosAuthenticationClientImpl.class);
	
	private @Autowired IKerberosAuthenticationAPI iKerberosAuthenticationAPI;
	private @Autowired ApplicationDetailService applicationDetailService;
	private @Autowired SessionDirectory sessionDirectory;
	private @Autowired KerberosURLConfig kerberosURLConfig;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	
	@Override
	public KerberosAppSession kerberosAuthentication() throws IOException, ResponseDecryptionException, ApplicationDetailServiceUninitializedException, RestClientException{

		log.debug("Entering authenticateApp method");
		
		String appLoginName = applicationDetailService.getAppLoginName();
		String password = applicationDetailService.getAppPassword();
		
		if (!iEncryptionUtil.validateDecryptedAttributes(appLoginName, password)) {
			log.error("Invalid app credentials. AppLoginName and Password can not be empty or null");
			throw new ApplicationDetailServiceUninitializedException();
		}
		
		Map<AuthenticationResponseAttributes, String> responseAttributes = iKerberosAuthenticationAPI.authenticate(kerberosURLConfig.getKERBEROS_APP_AUTHENTICATION_URL(), appLoginName, password);
		
		if (responseAttributes == null){
			return null;
		}
		
		String sessionKey = responseAttributes.get(AuthenticationResponseAttributes.SESSION_KEY);
		String TGTPacket = responseAttributes.get(AuthenticationResponseAttributes.TGT_PACKET);
		
		log.debug("Returning from authenticateApp method");
		
		return sessionDirectory.createKerberosAppSession(sessionKey, TGTPacket);
		
	}
	
	
}
