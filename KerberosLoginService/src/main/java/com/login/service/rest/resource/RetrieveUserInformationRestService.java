/**
 * 
 */
package com.login.service.rest.resource;

import java.io.IOException;
import java.util.Map;

import javax.management.InvalidAttributeValueException;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.rest.exceptions.InvalidInputException;
import com.login.rest.exceptions.InvalidRequestException;
import com.login.rest.exceptions.ServiceUnavailableException;
import com.login.service.rest.api.IAppAccessServiceAPI;
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
	
	private @Autowired IAppAccessServiceAPI iAppAccessServiceAPI;
	private @Autowired IActiveDirectory iActiveDirectory;
	
	@POST
	@Path("/user/information")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AppAccessServiceResponse getUserInformation(AppAccessServiceRequest request){
		
		log.debug("Entering getUserInformation");
		
		Map<String, String> requestData = null;
		try {
			requestData = iAppAccessServiceAPI.processAppAccessServiceRequest(request);
		} catch (InvalidAttributeValueException e) {
			e.printStackTrace();
		}
		
		String username = requestData.get("uid");
		String retrieveAttributes = requestData.get("RETRIEVE_ATTRIBUTES");
		
		if (username == null || retrieveAttributes == null || username.isEmpty() | retrieveAttributes.isEmpty()){
			throw new InvalidRequestException("Invalid Input Data Provided", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML);
		}
		
		String[] attributes = retrieveAttributes.split(",");
		Map<String, String> userInfo = null;
		try {
			userInfo = iActiveDirectory.getUserInfoForService(request.getAppID(), username, attributes);
		} catch (IOException e1) {
			log.error("Error getting user information from directory\n"+e1.getMessage());
			e1.printStackTrace();
			throw new ServiceUnavailableException("Error processing request. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		} catch (NamingException e1) {
			log.error("Invalid Input provided to getUserInfoForService. Error fetching user information\n"+e1.getMessage());
			e1.printStackTrace();
			throw new InvalidInputException("Invalid parameters provided to retireve user information", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML);
		}
		
		try {
			return iAppAccessServiceAPI.generateAppAccessServiceResponse(request, userInfo);
		} catch (InvalidAttributeValueException e) {
			log.error("Error generating AppAccessServiceResponse");
			e.printStackTrace();
			throw new ServiceUnavailableException("Error processing request. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
	}

}
