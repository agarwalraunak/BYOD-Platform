/**
 * 
 */
package com.service.kerberos.rest.client;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.config.KerberosURLConfig;
import com.service.config.applicationdetailservice.ApplicationDetailService;
import com.service.kerberos.rest.api.IKerberosAuthenticationAPI;
import com.service.kerberos.rest.api.KerberosAuthenticationAPIImpl.AuthenticationResponseAttributes;
import com.service.model.SessionDirectory;
import com.service.model.kerberos.KerberosAppSession;
import com.service.rest.exception.common.InternalSystemException;

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
	
	/**
	 * @return boolean true if application was authenticated successfully
	 * @throws InternalSystemException 
	 */
	@Override
	public KerberosAppSession kerberosAuthentication() throws InternalSystemException {

		log.debug("Entering authenticateApp method");
		
		//Check if the Session for Application already exists
		KerberosAppSession kerberosAppSession = sessionDirectory.getKerberosAppSession();
		if (kerberosAppSession != null){
			return kerberosAppSession;
		}
		
		//Get the Application Login Name and Password
		String appLoginName = applicationDetailService.getAppLoginName();
		String password = applicationDetailService.getAppPassword();
		
		
		Map<AuthenticationResponseAttributes, String> responseAttributes = iKerberosAuthenticationAPI.authenticate(kerberosURLConfig.getKERBEROS_APP_AUTHENTICATION_URL(), appLoginName, password, true);
		if (responseAttributes == null){
			return null;
		}
		
		String sessionKey = responseAttributes.get(AuthenticationResponseAttributes.SESSION_KEY);
		String TGTPacket = responseAttributes.get(AuthenticationResponseAttributes.TGT_PACKET);
		
		kerberosAppSession = sessionDirectory.createKerberosAppSession(sessionKey, TGTPacket);
		
		
		log.debug("Returning from authenticateApp method");
		
		return kerberosAppSession;
	}
	
}
