package com.kerberos.client.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.InvalidAttributeValueException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kerberos.device.model.AppServiceSession;
import com.kerberos.device.model.KerberosSessionManager;
import com.kerberos.device.model.ServiceTicket;
import com.kerberos.device.model.UserServiceSession;
import com.kerberos.device.rest.client.AccessServiceClient;
import com.kerberos.device.rest.client.KerberosAuthenticationClient;
import com.kerberos.device.rest.client.KerberosRequestServiceTicketClient;
import com.kerberos.device.rest.exceptions.UnauthorizedResponseException;
import com.kerberos.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.kerberos.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

@Controller
@RequestMapping("/test")
public class TestController {
	
	private @Autowired KerberosAuthenticationClient client;
	private @Autowired KerberosRequestServiceTicketClient rstClient;
	private @Autowired KerberosSessionManager kerberosSessionManager;
	private @Autowired AccessServiceClient accessServiceClient;
	
	@RequestMapping(method=org.springframework.web.bind.annotation.RequestMethod.GET)
	public String test() throws IOException{
		try {
			//Authenticating the app
			if (client.authenticateApp()){
				//Kerberos Session ID
				System.out.println(kerberosSessionManager.getAppSession().getSessionID());
				//Get the Service Ticket for Service from Kerberos
				ServiceTicket ticket = rstClient.getServiceTicketForApp("ServiceSecurity");
				//App and Service Mutual Authentication
				AppServiceSession appServiceSession = accessServiceClient.authenticateAppServiceTicket("http://localhost:8080/service/orange/authenticate/app/serviceTicket/", ticket);
				//Service User Authentication
				UserServiceSession serviceSession = accessServiceClient.authenticateUser("http://localhost:8080/service/orange/authenticate/user/", ticket, "Sam.Bolt@gmail.com", "testPassword");
				//Service Session ID's for app and service
				System.out.println(appServiceSession.getSessionID());
				System.out.println(serviceSession.getUserSessionID());
				
				//Sending data from app to service
				Map<String, String> data = new HashMap<>();
				data.put("test", "testValue");
				data = accessServiceClient.accessService("http://localhost:8080/service/orange/test123/restservice/", RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, ticket, "Sam.Bolt@gmail.com", data);
				//Looping through the response data
				Iterator<String> iterator = data.keySet().iterator();
				String skey = null;
				while(iterator.hasNext()){
					skey = iterator.next();
					System.out.println("Service Response Data Key: "+skey+" :: Value: "+data.get(skey));
				}
				return "redirect:http://www.google.com";
			}
		} catch (InvalidAttributeValueException | UnauthorizedResponseException e) {
			e.printStackTrace();
		}
		return "redirect:http://www.google.com";
	}

}
