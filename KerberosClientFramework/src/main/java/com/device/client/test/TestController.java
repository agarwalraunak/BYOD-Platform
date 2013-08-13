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

import com.device.config.KerberosURLConfig;
import com.device.config.ServiceListConfig;
import com.device.kerberos.model.KerberosSessionManager;
import com.device.kerberos.model.ServiceTicket;
import com.device.kerberos.rest.client.KerberosAuthenticationClient;
import com.device.kerberos.rest.client.KerberosRequestServiceTicketClient;
import com.device.login.rest.client.LoginServerUserAuthenticationClient;
import com.device.rest.exceptions.UnauthorizedRequestException;
import com.device.rest.exceptions.UnauthorizedResponseException;
import com.device.service.model.AppSession;
import com.device.service.model.UserSession;
import com.device.service.rest.client.AccessServiceClient;
import com.device.service.rest.client.ServiceAppAuthenticationClient;
import com.device.service.rest.client.ServiceUserAuthenticationClient;
import com.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

@Controller
@RequestMapping("/test")
public class TestController {
	
	private @Autowired KerberosAuthenticationClient client;
	private @Autowired KerberosRequestServiceTicketClient rstClient;
	private @Autowired KerberosSessionManager kerberosSessionManager;
	private @Autowired ServiceAppAuthenticationClient serviceAppAuthenticationClient;
	private @Autowired ServiceUserAuthenticationClient serviceUserAuthenticationClient;
	private @Autowired LoginServerUserAuthenticationClient loginServerUserAuthenticationClient;
	private @Autowired KerberosURLConfig kerberosURLConfig;
	private @Autowired AccessServiceClient accessServiceClient;
	
	private static Logger log = Logger.getLogger(TestController.class);
	
	@RequestMapping(method=org.springframework.web.bind.annotation.RequestMethod.GET)
	public String test() throws IOException{
		try {
			//Kerberos App Authentication
			log.debug(" Performing App Kerberos Authentication");
			if (client.authenticateApp()){
				//Kerberos Session ID
				System.out.println(kerberosSessionManager.getAppSession().getSessionID());
				//Get the Service Ticket for Service from Kerberos
				log.debug(" Fetching Service Ticket for APP");
				ServiceTicket ticket = rstClient.getServiceTicketForApp(ServiceListConfig.LOGIN_SERVER.getValue());
				//App and Service Mutual Authentication
				log.debug(" Authenticating Login Server Service Ticket");
				AppSession appSession = serviceAppAuthenticationClient.authenticateAppServiceTicket(kerberosURLConfig.getLOGIN_SERVICE_APP_AUTHENTICATION_URL(), ticket);
				//Login Server User Authentication
				log.debug("Performing User Authentication at Login Server");
				UserSession serviceSession = loginServerUserAuthenticationClient.authenticateUser(kerberosURLConfig.getLOGIN_SERVICE_USER_AUTHENTICATION_URL(), appSession, ticket.getServiceSessionID(), "Sam.Bolt@gmail.com", "testPassword");
				//Service Session ID's for app and service
				System.out.println(appSession.getSessionID());
				System.out.println(serviceSession.getUserSessionID());
				
				
				//Get the Service Ticket for Service from Kerberos
				log.debug(" Fetching Service Ticket for ServiceSecurity");
				ServiceTicket serviceTicket = rstClient.getServiceTicketForApp("ServiceSecurity");
				//App and Service Mutual Authentication
				log.debug("Authenticating Service Security Service Ticket for APP");
				AppSession appServiceSession = serviceAppAuthenticationClient.authenticateAppServiceTicket("http://localhost:8080/service/orange/authenticate/app/serviceTicket/", serviceTicket);
				//Login Server User Authentication
				if (serviceUserAuthenticationClient.serviceUserAuthentication("http://localhost:8080/service/orange/authenticate/user/", serviceSession, appServiceSession)){
					System.out.println("Service User Authentication success");
				}
				else{
					System.out.println("Service User Authentication failed");
				}
				
				//Sending data from app to service
				Map<String, String> data = new HashMap<>();
				data.put("test", "testValue");
				log.debug("Access Service Security Service Ticket for APP");
				data = accessServiceClient.accessService("http://localhost:8080/service/orange/test123/restservice/", RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, serviceTicket, "Sam.Bolt@gmail.com", data);
				//Looping through the response data
				Iterator<String> iterator = data.keySet().iterator();
				String skey = null;
				while(iterator.hasNext()){
					skey = iterator.next();
					System.out.println("Service Response Data Key: "+skey+" :: Value: "+data.get(skey));
				}
				return "redirect:http://www.google.com";
			}
		} catch (InvalidAttributeValueException | UnauthorizedResponseException | UnauthorizedRequestException e) {
			e.printStackTrace();
		}
		return "redirect:http://www.google.com";
	}

}
