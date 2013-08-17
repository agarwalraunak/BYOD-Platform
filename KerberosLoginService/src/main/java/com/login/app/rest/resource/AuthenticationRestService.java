/**
 * 
 */
package com.login.app.rest.resource;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.app.rest.api.AppAuthenticationAPIImpl.ServiceTicketAttributes;
import com.login.app.rest.api.IAppAuthenticationAPI;
import com.login.app.rest.api.IUserAuthenticationAPI;
import com.login.app.rest.representation.AppAuthenticationRequest;
import com.login.app.rest.representation.AppAuthenticationResponse;
import com.login.app.rest.representation.UserLoginRequest;
import com.login.app.rest.representation.UserLoginResponse;
import com.login.config.KerberosURLConfig;
import com.login.exception.ApplicationDetailServiceUninitializedException;
import com.login.exception.ResponseDecryptionException;
import com.login.exception.RestClientException;
import com.login.exception.AuthenticationRestService.DecryptUserLoginRequestParamsException;
import com.login.exception.AuthenticationRestService.DecryptedServiceTicketPacketValidationException;
import com.login.exception.AuthenticationRestService.DecryptionServiceTicketPacketException;
import com.login.exception.common.AuthenticatorValidationException;
import com.login.exception.common.InternalSystemException;
import com.login.exception.common.InvalidRequestException;
import com.login.exception.common.UnauthenticatedAppException;
import com.login.kerberos.rest.api.IKerberosServiceRequestAPI;
import com.login.kerberos.rest.api.KerberosAuthenticationAPIImpl.SecretKeyType;
import com.login.kerberos.rest.client.IKerberosAuthenticationClient;
import com.login.kerberos.rest.client.IKerberosKeyServerClient;
import com.login.kerberos.rest.client.IKerberosRequestServiceTicketClient;
import com.login.model.SessionDirectory;
import com.login.model.app.AppSession;
import com.login.model.app.UserSession;
import com.login.model.kerberos.KerberosAppSession;
import com.login.model.kerberos.ServiceTicket;
import com.login.session.SessionManagementAPIImpl.RequestParam;
import com.login.util.dateutil.IDateUtil;
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
	
	private @Autowired IKerberosServiceRequestAPI iKerberosServiceRequestAPI;
	private @Autowired IUserAuthenticationAPI iUserAuthenticationClientAPI;
	
	private @Autowired IKerberosKeyServerClient iKerberosKeyServerClient;
	private @Autowired IKerberosRequestServiceTicketClient iKerberosRequestServiceTicketClient;
	private @Autowired IKerberosAuthenticationClient iKerberosAuthenticationClient;

	private @Autowired SessionDirectory sessionDirectory;
	
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil; 
	private @Autowired IHashUtil iHashUtil;
	
	private @Autowired IAppAuthenticationAPI iAppAuthenticationAPI;
	
	private @Autowired KerberosURLConfig kerberosURLConfig;
	
	@Path("/app/serviceTicket")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AppAuthenticationResponse authenticateApp(AppAuthenticationRequest request, @Context HttpServletRequest httpRequest) throws InternalSystemException, DecryptionServiceTicketPacketException, DecryptedServiceTicketPacketValidationException, AuthenticatorValidationException {
		
		log.debug("Entering authenticateApp method");
		
		//Authenticate the Service Application
		KerberosAppSession kerberosAppSession;
		try {
			kerberosAppSession = iKerberosAuthenticationClient.kerberosAuthentication();
		} catch (IOException | RestClientException	| ResponseDecryptionException | ApplicationDetailServiceUninitializedException e) {
			log.error("Error performing kerberos authentication\n"+e.getMessage());
			e.printStackTrace();
			throw new InternalSystemException();
		}
		if (kerberosAppSession == null){
			log.error("Kerberos Authentication of the System failed!");
			throw new InternalSystemException();
		}
		//Get the service ticket for Key server
		ServiceTicket serviceTicket = null;
		try {
			serviceTicket = iKerberosRequestServiceTicketClient.getServiceTicketForApp(SecretKeyType.KEY_SERVER.getValue(), kerberosAppSession);
		} catch (IOException | RestClientException e) {
			log.error("Error fetching Key Server service ticket\n"+e.getMessage());
			e.printStackTrace();
			throw new InternalSystemException();
		}
		if (serviceTicket == null){
			log.error("failed to get the Service Ticket for key server!");
			throw new InternalSystemException();
		}
		
		//Get the service key from key server
		SecretKey serviceKey;
		try {
			serviceKey = iKerberosKeyServerClient.getKeyFromKeyServer(serviceTicket, SecretKeyType.SERVICE_KEY);
		} catch (RestClientException | IOException e) {
			log.error("Error fetching Service Key from key server");
			e.printStackTrace();
			throw new InternalSystemException();
		}
		if (serviceKey == null){
			log.error("Failed to get the key from key server!");
			throw new InternalSystemException();
		}
		
		String encServiceTicketPacket = request.getServiceTicketPacket();
		String encAuthenticator = request.getEncAuthenticator();
		
		//Decrypt the Service Ticket Packet
		 Map<ServiceTicketAttributes, String> decryptedServiceTicketPacket = iAppAuthenticationAPI.decryptServiceTicketPacket(encServiceTicketPacket, serviceKey);
		 if (decryptedServiceTicketPacket == null){
			 throw new DecryptionServiceTicketPacketException();
		 }
		 
		String username = decryptedServiceTicketPacket.get(ServiceTicketAttributes.USERNAME);
		String serviceSessionID = decryptedServiceTicketPacket.get(ServiceTicketAttributes.SERVICE_SESSION_ID);
		String serviceTicketExpirationString = decryptedServiceTicketPacket.get(ServiceTicketAttributes.SERVICE_TICKET_EXPIRATION_TIME_STR);
		
		//Validating the Decrypted Service Ticket Packet
		if (!iAppAuthenticationAPI.validateServiceTicket(username, serviceSessionID, serviceTicketExpirationString)){
			log.error("Validation of Decrypted Service Ticket Packet failed!");
			throw new DecryptedServiceTicketPacketValidationException();
		}
		
		//Decrypt the Authenticator
		SecretKey serviceSessionKey = iEncryptionUtil.generateSecretKey(serviceSessionID);
		String requestAuthenticatorStr = iEncryptionUtil.decrypt(serviceSessionKey, encAuthenticator)[0];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		
		//Validate the Authenticator
		if (!iDateUtil.validateAuthenticator(requestAuthenticator)){
			throw new AuthenticatorValidationException();
		}
		
		//Check if the session for the application already exists else create
		AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(username);
		if (appSession == null){
			appSession = sessionDirectory.createAppSession(serviceSessionID, username, httpRequest.getRemoteAddr());
		}
				
		//Creating AppAuthenticationResponse
		AppAuthenticationResponse response = iAppAuthenticationAPI.createAppAuthenticationResponse(appSession, requestAuthenticator, httpRequest.getServletPath(), serviceSessionKey);

		return response;
	}
	
	@Path("/user")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserLoginResponse authenticateUser(UserLoginRequest request, @Context HttpServletRequest httpRequest) throws InvalidRequestException, UnauthenticatedAppException, AuthenticatorValidationException, DecryptUserLoginRequestParamsException, InternalSystemException{
		
		log.debug("Entering authenticateUser method");
		
		//Validate the incoming request
		if (!iEncryptionUtil.validateDecryptedAttributes(request.getEncUsername(), request.getEncPassword(), request.getEncAppSessionID(), request.getEncAuthenticator(), request.getAppID())){
			log.error("Invalid input parameter provided to processUserLoginRequest");
			throw new InvalidRequestException();
		}
		
		AppSession appSession = (AppSession)httpRequest.getAttribute(RequestParam.APP_SESSION.getValue());
		
		UserSession userSession = iUserAuthenticationClientAPI.authenticateUser(request.getEncUsername(), request.getEncPassword(), httpRequest.getRemoteAddr(), appSession);
		
		return iUserAuthenticationClientAPI.createUserLoginResponse(request.getEncUsername(), userSession);
	}
}