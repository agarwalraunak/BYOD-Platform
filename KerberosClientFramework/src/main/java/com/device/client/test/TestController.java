package com.device.client.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.device.config.KerberosURLConfig;
import com.device.config.ServiceListConfig;
import com.device.exception.ApplicationDetailServiceUninitializedException;
import com.device.exception.InvalidResponseAuthenticatorException;
import com.device.exception.ResponseDecryptionException;
import com.device.exception.RestClientException;
import com.device.exception.UnauthenticatedUserException;
import com.device.exception.UnauthorizedResponseException;
import com.device.kerberos.model.KerberosSessionManager;
import com.device.kerberos.model.ServiceTicket;
import com.device.kerberos.rest.client.IKerberosAuthenticationClient;
import com.device.kerberos.rest.client.IKerberosRequestServiceTicketClient;
import com.device.login.rest.client.ILoginServerUserAuthenticationClient;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;
import com.device.service.rest.client.AccessServiceClientImpl;
import com.device.service.rest.client.IServiceAppAuthenticationClient;
import com.device.service.rest.client.IServiceUserAuthenticationClient;
import com.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

@Controller
@RequestMapping("/test")
public class TestController {
	
	private @Autowired IKerberosAuthenticationClient iKerberosAuthenticationClient;
	private @Autowired IKerberosRequestServiceTicketClient iKerberosRequestServiceTicketClient;
	private @Autowired KerberosSessionManager kerberosSessionManager;
	private @Autowired IServiceAppAuthenticationClient iServiceAppAuthenticationClient;
	private @Autowired IServiceUserAuthenticationClient iServiceUserAuthenticationClient;
	private @Autowired ILoginServerUserAuthenticationClient iLoginServerUserAuthenticationClient;
	private @Autowired KerberosURLConfig kerberosURLConfig;
	private @Autowired AccessServiceClientImpl accessServiceClientImpl;
	
	private static Logger log = Logger.getLogger(TestController.class);
	
	@RequestMapping(method=org.springframework.web.bind.annotation.RequestMethod.GET)
	public @ResponseBody String test() throws IOException, ResponseDecryptionException, ApplicationDetailServiceUninitializedException, UnauthorizedResponseException, InvalidResponseAuthenticatorException, UnauthenticatedUserException, InvalidAttributeValueException {
		//Kerberos App Authentication
		log.debug(" Performing App Kerberos Authentication");
		try {
			if (iKerberosAuthenticationClient.kerberosAuthentication()){
				//Kerberos Session ID
				System.out.println(kerberosSessionManager.getKerberosAppSession().getSessionID());

				//Get the Service Ticket for Service from Kerberos
				log.debug(" Fetching Service Ticket for APP");
				ServiceTicket ticket = iKerberosRequestServiceTicketClient.getServiceTicketForApp(ServiceListConfig.LOGIN_SERVER.getValue(), kerberosSessionManager.getKerberosAppSession());
				if (ticket == null){
					System.out.println("Failed to get the service ticket for login server");
				} else{
					System.out.println("Got the service ticket for login server");
				}

				//App and Service Mutual Authentication
				log.debug(" Authenticating Login Server Service Ticket");
				AppSession appSession = iServiceAppAuthenticationClient.authenticateAppServiceTicket(kerberosURLConfig.getLOGIN_SERVICE_APP_AUTHENTICATION_URL(), ticket);
				if (appSession != null)
					System.out.println("App Login Service Authentication success "+appSession.getSessionID());
				else{
					System.out.println("App Login Service authentication failed");
				}
				
				//Login Server User Authentication
				log.debug("Performing User Authentication at Login Server");
				UserSession userLoginServerSession = iLoginServerUserAuthenticationClient.authenticateUser(kerberosURLConfig.getLOGIN_SERVICE_USER_AUTHENTICATION_URL(), appSession, ticket.getServiceSessionID(), "Sam.Bolt@gmail.com", "testPassword");
				//Service Session ID's for app and service
				if (userLoginServerSession != null)
					System.out.println("User Login service authentication successfull "+userLoginServerSession.getUserSessionID());
				else
					System.out.println("User Login Service Authentication failed");
				
				
				//Get the Service Ticket for Service from Kerberos
				log.debug(" Fetching Service Ticket for ServiceSecurity");
				ServiceTicket serviceTicket = iKerberosRequestServiceTicketClient.getServiceTicketForApp("ServiceSecurity", kerberosSessionManager.getKerberosAppSession());
				if (serviceTicket == null){
					System.out.println("Failed to get the service ticket for service");
				} else{
					System.out.println("Got the service ticket for service");
				}
				
				//App and Service Mutual Authentication
				log.debug("Authenticating Service Security Service Ticket for APP");
				AppSession appServiceSession = iServiceAppAuthenticationClient.authenticateAppServiceTicket("http://localhost:8080/service/orange/authenticate/app/serviceTicket/", serviceTicket);
				if (appServiceSession == null){
					System.out.println("App and Service Mutual authentication failed");
				} else{
					System.out.println("App and Service Mutual authentication success "+appServiceSession.getSessionID());
				}
				
				//Login Server User Authentication
				if (iServiceUserAuthenticationClient.serviceUserAuthentication("http://localhost:8080/service/orange/authenticate/user/", userLoginServerSession, appServiceSession)){
					System.out.println("Service User Authentication success");
				}
				else{
					System.out.println("Service User Authentication failed");
				}
				
				//Sending data from app to service
				Map<String, String> data = new HashMap<>();
				data.put("test", "testValue");
				log.debug("Access Service Security Service Ticket for APP");
				data = accessServiceClientImpl.accessService("http://localhost:8080/service/orange/test123/restservice/", RequestMethod.POST_REQUEST_METHOD, 
						ContentType.APPLICATION_JSON, appServiceSession, serviceTicket.getServiceSessionID(), 
						appServiceSession.findActiveUserServiceSessionByUsername("Sam.Bolt@gmail.com"), data);
				//Looping through the response data
				Iterator<String> iterator = data.keySet().iterator();
				String skey = null;
				while(iterator.hasNext()){
					skey = iterator.next();
					System.out.println("Service Response Data Key: "+skey+" :: Value: "+data.get(skey));
				}
				return "redirect:http://www.google.com";
			}
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Message: "+e.getErrorMessage()+" Error Code: "+e.getErrorCode();
		}

		return "Tests were successfull";
	}

}
