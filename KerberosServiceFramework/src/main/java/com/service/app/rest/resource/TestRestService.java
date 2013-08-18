/**
 * 
 */
package com.service.app.rest.resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.app.rest.representation.UserAccessServiceRequest;
import com.service.app.rest.representation.UserAccessServiceResponse;
import com.service.config.KerberosURLConfig;
import com.service.config.ServiceListConfig;
import com.service.exception.ApplicationDetailServiceUninitializedException;
import com.service.exception.ResponseDecryptionException;
import com.service.exception.RestClientException;
import com.service.exception.common.AuthenticatorValidationException;
import com.service.exception.common.InternalSystemException;
import com.service.exception.common.UnauthenticatedAppException;
import com.service.exception.common.UnauthenticatedUserException;
import com.service.kerberos.rest.client.IKerberosAuthenticationClient;
import com.service.kerberos.rest.client.IKerberosServiceTicketClient;
import com.service.model.app.AppSession;
import com.service.model.app.UserSession;
import com.service.model.kerberos.KerberosAppSession;
import com.service.model.kerberos.ServiceTicket;
import com.service.service.rest.client.IServiceAccessAnotherServiceClient;
import com.service.service.rest.client.IServiceAppAuthenticationClient;
import com.service.service.rest.representation.AppAccessServiceRequest;
import com.service.service.rest.representation.AppAccessServiceResponse;
import com.service.session.SessionManagementAPIImpl.RequestParam;
import com.service.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.service.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

/**
 * @author raunak
 *
 */

@Component
@Path("/test123/")
public class TestRestService {
	
	
	private @Autowired IServiceAccessAnotherServiceClient iServiceAccessAnotherServiceClient;
	private @Autowired IKerberosAuthenticationClient iKerberosAuthenticationClient;
	private @Autowired IKerberosServiceTicketClient iKerberosServiceTicketClient;
	private @Autowired IServiceAppAuthenticationClient iServiceAppAuthenticationClient;
	private @Autowired KerberosURLConfig kerberosURLConfig;
	
	@Path("/restservice/")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserAccessServiceResponse test(UserAccessServiceRequest request, @Context HttpServletRequest httpRequest) throws UnauthenticatedAppException, UnauthenticatedUserException, AuthenticatorValidationException, IOException, RestClientException, ResponseDecryptionException, ApplicationDetailServiceUninitializedException, InternalSystemException{
		
		Map<String, String> decData = request.getData();
		
		AppSession appSession = (AppSession)httpRequest.getAttribute(RequestParam.APP_SESSION.getValue());
		UserSession userSession = (UserSession)httpRequest.getAttribute(RequestParam.USER_SESSION.getValue());
		System.out.println("Service Side AppSession "+appSession.getSessionID());
		System.out.println("Service Side UserSession "+userSession.getSessionID());
		
		Iterator<String> iterator = decData.keySet().iterator();
		String key = null;
		while(iterator.hasNext()){
			key = iterator.next();
			System.out.println("Key: "+key+" :: Value: "+decData.get(key));
		}
		
		Map<String, String> responseData = new HashMap<String, String>();
		responseData.put("raunak", "agarwal");
		
		KerberosAppSession kerberosAppSession = iKerberosAuthenticationClient.kerberosAuthentication();
		ServiceTicket serviceTicket = iKerberosServiceTicketClient.getServiceTicketForApp(ServiceListConfig.LOGIN_SERVER.getValue(), kerberosAppSession);
		iServiceAppAuthenticationClient.authenticateAppServiceTicket(kerberosURLConfig.getLOGIN_SERVER_APP_AUTHENTICATION_URL(), serviceTicket);
		
		
		Map<String, String> requestData = new HashMap<>();
		
		requestData.put("uid", "Sam.Bolt@gmail.com");
		requestData.put("RETRIEVE_ATTRIBUTES", "cn,sn,title,employeeType");
		
		Map<String, String> responseData1 = null;
		try {
			responseData1 = iServiceAccessAnotherServiceClient.contactAnotherService(kerberosURLConfig.getLOGIN_SERVER_RETRIEVE_USER_INFO_URL(), RequestMethod.POST_REQUEST_METHOD, ContentType.APPLICATION_JSON, 
					ServiceListConfig.LOGIN_SERVER.getValue(), serviceTicket.getServiceSessionID(), serviceTicket.getServiceSession(), requestData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Iterator<String> iterator1 = responseData1.keySet().iterator();
		String key1 = null;
		while(iterator1.hasNext()){
			key1 = iterator1.next();
			System.out.println("KEY: "+key1+":: Value: "+responseData1.get(key1));
		}
		
		UserAccessServiceResponse response = new UserAccessServiceResponse();
		response.setData(requestData);
		return response;
	}	
	
	@Path("/app/restservice/")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AppAccessServiceResponse testAppAccessServiceRequest(AppAccessServiceRequest request, @Context HttpServletRequest httpRequest) throws UnauthenticatedAppException, UnauthenticatedUserException, AuthenticatorValidationException, IOException, RestClientException, ResponseDecryptionException, ApplicationDetailServiceUninitializedException, InternalSystemException{
	
		Map<String, String> decData = request.getData();
		Iterator<String> iterator = decData.keySet().iterator();
		String key = null;
		while(iterator.hasNext()){
			key = iterator.next();
			System.out.println("Key: "+key+" :: Value: "+decData.get(key));
		}
		
		Map<String, String> requestData = new HashMap<>();
		
		requestData.put("AppAccessServiceResponse", "Response Data");
		
		AppAccessServiceResponse response = new AppAccessServiceResponse();
		response.setEncResponseData(requestData);
		return response;
	}
}