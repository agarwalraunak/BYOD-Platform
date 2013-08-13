/**
 * 
 */
package com.service.app.rest.resource;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.app.rest.api.AppAuthenticationAPIImpl.ServiceTicketAttributes;
import com.service.app.rest.api.IAppAuthenticationAPI;
import com.service.app.rest.api.IUserAuthenticationAPI;
import com.service.app.rest.representation.AppAuthenticationRequest;
import com.service.app.rest.representation.AppAuthenticationResponse;
import com.service.app.rest.representation.UserServiceAuthenticationRequest;
import com.service.app.rest.representation.UserServiceAuthenticationResponse;
import com.service.config.KerberosURLConfig;
import com.service.config.ServiceListConfig;
import com.service.kerberos.rest.api.KerberosAuthenticationAPIImpl.SecretKeyType;
import com.service.kerberos.rest.client.IKerberosAuthenticationClient;
import com.service.kerberos.rest.client.IKerberosKeyServerClient;
import com.service.kerberos.rest.client.IKerberosServiceTicketClient;
import com.service.model.SessionDirectory;
import com.service.model.app.AppSession;
import com.service.model.app.UserSession;
import com.service.model.kerberos.KerberosAppSession;
import com.service.model.kerberos.ServiceTicket;
import com.service.model.service.ServiceSession;
import com.service.rest.exception.AppAuthenticationRestService.DecryptedServiceTicketPacketValidationException;
import com.service.rest.exception.AppAuthenticationRestService.DecryptionServiceTicketPacketException;
import com.service.rest.exception.common.AuthenticatorValidationException;
import com.service.rest.exception.common.InternalSystemException;
import com.service.rest.exception.common.InvalidRequestException;
import com.service.rest.exception.common.UnauthenticatedAppException;
import com.service.service.login.rest.client.ILoginServerValidateUserAuthenticationClient;
import com.service.service.rest.client.IServiceAppAuthenticationClient;
import com.service.util.dateutil.IDateUtil;
import com.service.util.encryption.IEncryptionUtil;
import com.service.util.hashing.IHashUtil;

/**
 * @author raunak
 *
 */
@Component
@Path("/authenticate")
public class AuthenticationRestService {
	
	private static Logger log = Logger.getLogger(AuthenticationRestService.class);
	
	private @Autowired IKerberosKeyServerClient iKerberosKeyServerClient;
	private @Autowired IKerberosServiceTicketClient iKerberosServiceTicketClient;
	private @Autowired IKerberosAuthenticationClient iKerberosAuthenticationClient;
	private @Autowired IServiceAppAuthenticationClient iServiceAppAuthenticationClient;
	private @Autowired ILoginServerValidateUserAuthenticationClient iLoginServerValidateUserAuthenticationClient;
	
	private @Autowired SessionDirectory sessionDirectory;
	
	private @Autowired IDateUtil iDateUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil; 
	private @Autowired IHashUtil iHashUtil;
	
	private @Autowired IUserAuthenticationAPI iUserAuthenticationAPI;
	private @Autowired IAppAuthenticationAPI iAppAuthenticationAPI;
	
	private @Autowired KerberosURLConfig kerberosURLConfig;
	
	/**
	 * Authenticates the App 
	 * @param request: AppAuthenticationRequest
	 * @return
	 * @throws InternalSystemException 
	 * @throws DecryptionServiceTicketPacketException 
	 * @throws DecryptedServiceTicketPacketValidationException 
	 * @throws AuthenticatorValidationException 
	 */
	@Path("/app/serviceTicket")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AppAuthenticationResponse authenticateApp(AppAuthenticationRequest request) throws InternalSystemException, DecryptionServiceTicketPacketException, DecryptedServiceTicketPacketValidationException, AuthenticatorValidationException {
		
		log.debug("Entering authenticateApp method");
		
		//Authenticate the Service Application
		KerberosAppSession kerberosAppSession = iKerberosAuthenticationClient.kerberosAuthentication();
		if (kerberosAppSession == null){
			log.error("Kerberos Authentication of the System failed!");
			throw new InternalSystemException();
		}
		//Get the service ticket for Key server
		ServiceTicket serviceTicket = iKerberosServiceTicketClient.getServiceTicketForApp(SecretKeyType.KEY_SERVER.getValue(), kerberosAppSession);
		if (serviceTicket == null){
			log.error("failed to get the Service Ticket for key server!");
			throw new InternalSystemException();
		}
		
		//Get the service key from key server
		SecretKey serviceKey = iKerberosKeyServerClient.getKeyFromKeyServer(serviceTicket, SecretKeyType.SERVICE_KEY);
		
		
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
		AppSession appSession = sessionDirectory.findAppSessionByAppID(username);
		if (appSession == null){
			appSession = sessionDirectory.createAppSession(serviceSessionID, username);
		}
		//Add the authenticator to the App Session
		appSession.addAuthenticator(requestAuthenticator);
				
		//Creating AppAuthenticationResponse
		AppAuthenticationResponse response = iAppAuthenticationAPI.createAppAuthenticationResponse(appSession, requestAuthenticator, serviceSessionKey);

		return response;
	}
	
	/**
	 * This rest service validates the user authentication by looking up if the Service already has User Session created.
	 * Otherwise, contacts the Login Server, to check if the user is authenticated. If user is authenticated successfully, creates 
	 * a session else returns an error 
	 * @param request
	 * @return
	 * @throws InvalidRequestException 
	 * @throws UnauthenticatedAppException 
	 * @throws AuthenticatorValidationException 
	 * @throws InternalSystemException 
	 */
	@Path("/user/")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public UserServiceAuthenticationResponse userServiceAuthentication(UserServiceAuthenticationRequest request) throws InvalidRequestException, UnauthenticatedAppException, AuthenticatorValidationException, InternalSystemException{
		
		log.debug("Entering userServiceAuthentication");
		
		String appLoginName = request.getAppID();
		String encAuthenticator = request.getEncAuthenticator();
		String encUserLoginServerSessionID = request.getEncUserSessionID();
		String encUsername = request.getEncUsername();
		
		//Validating the attributes of the request
		if (!iEncryptionUtil.validateDecryptedAttributes(appLoginName, encAuthenticator, encUserLoginServerSessionID, encUsername)){
			log.error("Invalid User Service Authentication Request");
			throw new InvalidRequestException();				
		}
		
		//Find the App Session
		AppSession appSession = sessionDirectory.findAppSessionByAppID(appLoginName);
		//If session does not exist throw an exception
		if (appSession == null){
			log.error("Couldn't find a session for the given App Login Name. Request from an Unauthenticated App");
			throw new UnauthenticatedAppException();
		}
		
		//Generating appSessionKey using the App Session ID
		SecretKey appSessionKey = iEncryptionUtil.generateSecretKey(appSession.getSessionID());
		
		//Decrypt and validate the request parameters
		String[] requestParams = iUserAuthenticationAPI.decrytAndValidateUserServiceAuthenticationRequestParameters(appSession, appSessionKey, request);
		
		String username = requestParams[0];
		String userLoginServerSessionID = requestParams[1];
		String requestAuthenticatorStr = requestParams[2];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		
		//Check if the User already has a session or not
		UserSession userSession = appSession.findUserSessionByUsername(username);
		//If Session Exists send the old session id
		if (userSession != null){
			//If users is authenticated generate a session id
			String userSessionID = userSession.getSessionID();
			UserServiceAuthenticationResponse response = null;
			response = iUserAuthenticationAPI.generateUserServiceAuthenticationResponse(requestAuthenticator, userSessionID, appSessionKey, appSession);
			if (response == null){
				log.error("Error generating UserServiceAuthenticationResponse");
				throw new InternalSystemException();
			}
			return response;
		}

		//If Session Does not exist validate the user authentication against Login Server
		//Get the service ticket for login server
		KerberosAppSession kerberosAppSession = iKerberosAuthenticationClient.kerberosAuthentication();
		ServiceTicket loginServiceTicket = iKerberosServiceTicketClient.getServiceTicketForApp(ServiceListConfig.LOGIN_SERVER.getValue(), kerberosAppSession);
		//Get the Service Session for login server
		ServiceSession loginServiceSession = iServiceAppAuthenticationClient.authenticateAppServiceTicket(kerberosURLConfig.getLOGIN_SERVER_APP_AUTHENTICATION_URL(), loginServiceTicket);
		
		//Validate if user is authenticated against login server
		boolean isAuthenticated = iLoginServerValidateUserAuthenticationClient.validateUserAuthenticationAgainstLoginServer(loginServiceSession, userLoginServerSessionID);
		
		//If users is authenticated create a session
		String userSessionID = null;
		if (isAuthenticated){
			userSessionID = iHashUtil.getSessionKey();
			appSession.createUserSession(username, userSessionID);
		}
		UserServiceAuthenticationResponse response = null;
		response = iUserAuthenticationAPI.generateUserServiceAuthenticationResponse(requestAuthenticator, userSessionID, appSessionKey, appSession);
		if (response == null){
			log.error("Error generating UserServiceAuthenticationResponse");
			throw new InternalSystemException();
		}
		
		return response;
	}
	
}