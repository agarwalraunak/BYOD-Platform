/**
 * 
 */
package com.service.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.service.config.KerberosURLConfig;
import com.service.config.ServiceListConfig;
import com.service.exception.ApplicationDetailServiceUninitializedException;
import com.service.exception.ResponseDecryptionException;
import com.service.exception.RestClientException;
import com.service.exception.common.AuthenticatorValidationException;
import com.service.exception.common.InternalSystemException;
import com.service.kerberos.rest.client.IKerberosAuthenticationClient;
import com.service.kerberos.rest.client.IKerberosServiceTicketClient;
import com.service.model.kerberos.KerberosAppSession;
import com.service.model.kerberos.ServiceTicket;
import com.service.service.rest.client.IServiceAccessAnotherServiceClient;
import com.service.service.rest.client.IServiceAppAuthenticationClient;
import com.service.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.service.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

/**
 * @author raunak
 *
 */
@Controller
public class ServiceAccessAnotherServiceClientTest {
	
	private @Autowired IServiceAccessAnotherServiceClient iServiceAccessAnotherServiceClient;
	private @Autowired IKerberosAuthenticationClient iKerberosAuthenticationClient;
	private @Autowired IKerberosServiceTicketClient iKerberosServiceTicketClient;
	private @Autowired IServiceAppAuthenticationClient iServiceAppAuthenticationClient;
	
	private @Autowired KerberosURLConfig kerberosURLConfig;
	
	private String loginServerRetrieveUserInfoURL = "http://localhost:8080/login/orange/retrieve/user/information/";
	
	
	@RequestMapping("/ServiceAccessAnotherServiceClientTest")
	public void test() throws InternalSystemException, AuthenticatorValidationException, IOException, RestClientException, ResponseDecryptionException, ApplicationDetailServiceUninitializedException{
		
		KerberosAppSession kerberosAppSession = iKerberosAuthenticationClient.kerberosAuthentication();
		ServiceTicket serviceTicket = iKerberosServiceTicketClient.getServiceTicketForApp(ServiceListConfig.LOGIN_SERVER.getValue(), kerberosAppSession);
		iServiceAppAuthenticationClient.authenticateAppServiceTicket(kerberosURLConfig.getLOGIN_SERVER_APP_AUTHENTICATION_URL(), serviceTicket);
		
		
		Map<String, String> requestData = new HashMap<>();
		
		requestData.put("uid", "Sam.Bolt@gmail.com");
		requestData.put("RETRIEVE_ATTRIBUTES", "cn,sn,title,employeeType");
		
		
		Map<String, String> responseData = null;
		try {
			responseData = iServiceAccessAnotherServiceClient.contactAnotherService(loginServerRetrieveUserInfoURL, RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, 
					ServiceListConfig.LOGIN_SERVER.getValue(), serviceTicket.getServiceSessionID(), serviceTicket.getServiceSession(), requestData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Iterator<String> iterator = responseData.keySet().iterator();
		String key = null;
		while(iterator.hasNext()){
			key = iterator.next();
			System.out.println("KEY: "+key+":: Value: "+responseData.get(key));
		}
		
		
	}

}
