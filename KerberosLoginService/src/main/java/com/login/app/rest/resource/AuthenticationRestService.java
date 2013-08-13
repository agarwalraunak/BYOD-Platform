/**
 * 
 */
package com.login.app.rest.resource;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.app.rest.api.IAuthenticationClientAPI;
import com.login.app.rest.representation.AppAuthenticationRequest;
import com.login.app.rest.representation.AppAuthenticationResponse;
import com.login.app.rest.representation.UserLoginRequest;
import com.login.app.rest.representation.UserLoginResponse;
import com.login.config.ServiceListConfig;
import com.login.kerberos.model.ServiceTicket;
import com.login.kerberos.rest.api.IKerberosServiceRequestAPI;
import com.login.kerberos.rest.client.KerberosKeyServerClient;
import com.login.kerberos.rest.client.KerberosServiceTicketClient;
import com.login.rest.exceptions.InvalidInputException;
import com.login.rest.exceptions.ServiceUnavailableException;
import com.login.util.ActiveDirectory.ActiveDirectoryImpl.SecretKeyType;
import com.login.util.encryption.IEncryptionUtil;
import com.login.util.hashing.IHashUtil;

/**
 * @author raunak
 *
 */
@Component
@Path("/authenticate")
public class AuthenticationRestService {
	
	private static Logger log = Logger.getLogger(AuthenticationRestService.class);
	
	private @Autowired KerberosKeyServerClient kerberosKeyServerClient;
	private @Autowired IKerberosServiceRequestAPI iKerberosServiceRequestAPI;
	private @Autowired KerberosServiceTicketClient kerberosServiceTicketClient;
	private @Autowired IAuthenticationClientAPI iAuthenticationClientAPI;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IHashUtil iHashUtil;
	
	@Path("/app/serviceTicket")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AppAuthenticationResponse authenticateApp(AppAuthenticationRequest request) throws InvalidAttributeValueException, InvalidInputException{
		
		log.debug("Entering authenticateApp method");
		
		ServiceTicket serviceTicket = iKerberosServiceRequestAPI.checkAppAuthenticationAndGetServiceTicket(ServiceListConfig.KEY_SERVER.getValue());
		
		SecretKey serviceKey = null;
		try {
			serviceKey = kerberosKeyServerClient.getKeyFromKeyServer(serviceTicket, SecretKeyType.SERVICE_KEY);
		} catch (InvalidAttributeValueException e) {
			log.error("Unable to get the key from key server. Detailed Exception is attached below: \n"+e.getMessage());
			e.printStackTrace();
			throw new ServiceUnavailableException("Error processing the request. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		if (serviceKey == null){
			log.error("Unable to get the key from key server");
			throw new ServiceUnavailableException("Error processing the request. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		return iAuthenticationClientAPI.processAppAuthenticationRequest(request.getServiceTicketPacket(), request.getEncAuthenticator(), serviceKey, null);
	}
	
	@Path("/user")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserLoginResponse authenticateUser(UserLoginRequest request){
		
		log.debug("Entering authenticateUser method");
		
		UserLoginResponse loginResponse = null;
		try {
			loginResponse = iAuthenticationClientAPI.processUserLoginRequest(request.getEncUsername(), request.getEncPassword(), request.getEncAppSessionID(), request.getEncAuthenticator(), request.getAppID());
		} catch (InvalidAttributeValueException e) {
			log.error("Invalid inout to processUserLoginRequest \n"+e.getMessage());
			e.printStackTrace();
			throw new InvalidInputException("Error processing request. Bad Request found!", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML);
		}
		
		if (loginResponse == null){
			throw new InvalidInputException("Error processing request. Bad Request found!", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML);
		}
		return loginResponse;
	}
}