/**
 * 
 */
package com.login.service.rest.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.exception.common.AuthenticatorValidationException;
import com.login.exception.common.InvalidRequestException;
import com.login.exception.common.UnauthenticatedAppException;
import com.login.model.SessionDirectory;
import com.login.service.rest.representation.ServiceValidateUserAuthenticationRequest;
import com.login.service.rest.representation.ServiceValidateUserAuthenticationResponse;
import com.login.util.dateutil.IDateUtil;
import com.login.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */
@Component
@Path("/validate")
public class ServiceValidateUserAuthenticationRestService {
	
	private static Logger log = Logger.getLogger(ServiceValidateUserAuthenticationRestService.class);
	
	private @Autowired SessionDirectory sessionDirectory;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	
	@Path("/user/authentication")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ServiceValidateUserAuthenticationResponse validateUserAuthentication(ServiceValidateUserAuthenticationRequest request) throws InvalidRequestException, UnauthenticatedAppException, AuthenticatorValidationException{
		
		log.debug("Entering validateUserAuthentication method");
		
		//Creating Response Attributes
		Boolean isAuthenticated = true;
		if (sessionDirectory.findActiveUserSessionBySessionID(request.getEncUserLoginSessionID()) == null){
			isAuthenticated = false;
		}
		
		//Creating the response
		ServiceValidateUserAuthenticationResponse response = new ServiceValidateUserAuthenticationResponse();
		response.setEncIsAuthenticated(isAuthenticated.toString());
		
		log.debug("Returning from validateUserAuthentication");
		
		return response;
	}
}