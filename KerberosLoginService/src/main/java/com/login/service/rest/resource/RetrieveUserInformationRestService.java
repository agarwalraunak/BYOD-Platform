/**
 * 
 */
package com.login.service.rest.resource;

import java.io.IOException;
import java.util.Map;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.exception.common.AuthenticatorValidationException;
import com.login.exception.common.InternalSystemException;
import com.login.exception.common.InvalidRequestException;
import com.login.exception.common.UnauthenticatedAppException;
import com.login.exception.common.UserDoesNotExistException;
import com.login.service.rest.representation.AppAccessServiceRequest;
import com.login.service.rest.representation.AppAccessServiceResponse;
import com.login.util.ActiveDirectory.IActiveDirectory;

/**
 * @author raunak
 *
 */
@Component
@Path("/retrieve")
public class RetrieveUserInformationRestService {
	
	private static Logger log = Logger.getLogger(RetrieveUserInformationRestService.class);
	
	private @Autowired IActiveDirectory iActiveDirectory;
	
	@POST
	@Path("/user/information")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AppAccessServiceResponse getUserInformation(AppAccessServiceRequest request) throws UserDoesNotExistException, InternalSystemException, InvalidRequestException, AuthenticatorValidationException, UnauthenticatedAppException{
		
		log.debug("Entering getUserInformation");
		
		Map<String, String> requestData = request.getData();
		
		String username = requestData.get("uid");
		String retrieveAttributes = requestData.get("RETRIEVE_ATTRIBUTES");
		
		if (username == null || retrieveAttributes == null || username.isEmpty() | retrieveAttributes.isEmpty()){
			throw new InvalidRequestException();
		}
		
		String[] attributes = retrieveAttributes.split(",");
		Map<String, String> userInfo = null;
		try {
			userInfo = iActiveDirectory.getUserInfoForService(request.getAppID(), username, attributes);
		} catch (IOException e1) {
			log.error("Error getting user information from directory\n"+e1.getMessage());
			e1.printStackTrace();
			throw new InternalSystemException();
		} catch (NamingException e1) {
			log.error("Error fetching user with the username. User with the username does not exist\n"+e1.getMessage());
			e1.printStackTrace();
			throw new UserDoesNotExistException();
		}
		
		AppAccessServiceResponse response = new AppAccessServiceResponse();
		response.setEncResponseData(userInfo);
		return response;
	}

}
