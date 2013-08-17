/**
 * 
 */
package com.service.app.rest.resource;

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

import com.service.app.rest.api.AppAuthenticationAPIImpl.ServiceTicketAttributes;
import com.service.app.rest.api.IAppAuthenticationAPI;
import com.service.app.rest.api.IUserAuthenticationAPI;
import com.service.app.rest.representation.AppAuthenticationRequest;
import com.service.app.rest.representation.AppAuthenticationResponse;
import com.service.app.rest.representation.UserServiceAuthenticationRequest;
import com.service.app.rest.representation.UserServiceAuthenticationResponse;
import com.service.config.KerberosURLConfig;
import com.service.config.ServiceListConfig;
import com.service.exception.ApplicationDetailServiceUninitializedException;
import com.service.exception.ResponseDecryptionException;
import com.service.exception.RestClientException;
import com.service.exception.AppAuthenticationRestService.DecryptedServiceTicketPacketValidationException;
import com.service.exception.AppAuthenticationRestService.DecryptionServiceTicketPacketException;
import com.service.exception.common.AuthenticatorValidationException;
import com.service.exception.common.InternalSystemException;
import com.service.exception.common.InvalidRequestException;
import com.service.exception.common.UnauthenticatedAppException;
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
	public AppAuthenticationResponse authenticateApp(AppAuthenticationRequest request, @Context HttpServletRequest httpRequest) throws InternalSystemException, 
	DecryptionServiceTicketPacketException, DecryptedServiceTicketPacketValidationException, AuthenticatorValidationException {
		
		log.debug("Entering authenticateApp method");
		
		//Authenticate the Service Application
		KerberosAppSession kerberosAppSession;
		try {
			kerberosAppSession = iKerberosAuthenticationClient.kerberosAuthentication();
		} catch (IOException | RestClientException
				| ResponseDecryptionException
				| ApplicationDetailServiceUninitializedException e) {
			log.error("Error performing kerberos authentication\n"+e.getMessage());
			e.printStackTrace();
			throw new InternalSystemException();
		}
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
		//Decrypt the Service Ticket Packet
		 Map<ServiceTicketAttributes, String> decryptedServiceTicketPacket = iAppAuthenticationAPI.decryptServiceTicketPacket(encServiceTicketPacket, serviceKey);
		 if (decryptedServiceTicketPacket == null){
			 throw new DecryptionServiceTicketPacketException();
		 }
		 
		String appLoginName = decryptedServiceTicketPacket.get(ServiceTicketAttributes.APP_LOGIN_NAME);
		String serviceSessionID = decryptedServiceTicketPacket.get(ServiceTicketAttributes.SERVICE_SESSION_ID);
		String serviceTicketExpirationString = decryptedServiceTicketPacket.get(ServiceTicketAttributes.SERVICE_TICKET_EXPIRATION_TIME_STR);
		
		//Validating the Decrypted Service Ticket Packet
		if (!iAppAuthenticationAPI.validateServiceTicket(appLoginName, serviceSessionID, serviceTicketExpirationString)){
			log.error("Validation of Decrypted Service Ticket Packet failed!");
			throw new DecryptedServiceTicketPacketValidationException();
		}
		
		String encAuthenticator = request.getEncAuthenticator();
		//Decrypt the Authenticator
		SecretKey serviceSessionKey = iEncryptionUtil.generateSecretKey(serviceSessionID);
		String requestAuthenticatorStr = iEncryptionUtil.decrypt(serviceSessionKey, encAuthenticator)[0];
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorStr);
		
		//Validate the Authenticator
		if (!iDateUtil.validateAuthenticator(requestAuthenticator)){
			throw new AuthenticatorValidationException();
		}
		
		//Check if the session for the application already exists else create
		AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(appLoginName);
		if (appSession == null){
			appSession = sessionDirectory.createAppSession(serviceSessionID, appLoginName, httpRequest.getRemoteAddr());
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
	public UserServiceAuthenticationResponse userServiceAuthentication(UserServiceAuthenticationRequest request, @Context HttpServletRequest httpRequest) throws InvalidRequestException, UnauthenticatedAppException, AuthenticatorValidationException, InternalSystemException{

		log.debug("Entering userServiceAuthentication");
		
		String appLoginName = request.getAppID();
		String userLoginServerSessionID = request.getEncUserSessionID();
		String username = request.getEncUsername();
		
		//Find the App Session
		AppSession appSession = sessionDirectory.findActiveAppSessionByAppID(appLoginName);
		//Check if the User already has a session or not
		UserSession userSession = appSession.findActiveUserSessionByUsername(username);		
		if (userSession == null){
			//If Session Does not exist validate the user authentication against Login Server
			//Get the service ticket for login server
			KerberosAppSession kerberosAppSession;
			try {
				kerberosAppSession = iKerberosAuthenticationClient.kerberosAuthentication();
			} catch (IOException | RestClientException | ResponseDecryptionException| ApplicationDetailServiceUninitializedException e) {
				log.error("Error performing kerberos authentication\n"+e.getMessage());
				e.printStackTrace();
				throw new InternalSystemException();
			}
			ServiceTicket loginServiceTicket = iKerberosServiceTicketClient.getServiceTicketForApp(ServiceListConfig.LOGIN_SERVER.getValue(), kerberosAppSession);
			//Get the Service Session for login server
			ServiceSession loginServiceSession = iServiceAppAuthenticationClient.authenticateAppServiceTicket(kerberosURLConfig.getLOGIN_SERVER_APP_AUTHENTICATION_URL(), loginServiceTicket);
			
			//Validate if user is authenticated against login server
			boolean isAuthenticated = iLoginServerValidateUserAuthenticationClient.validateUserAuthenticationAgainstLoginServer(loginServiceSession, loginServiceTicket.getServiceSessionID(), userLoginServerSessionID);
			
			//If users is authenticated create a session
			if (isAuthenticated){
				userSession = appSession.createUserSession(username, iHashUtil.getSessionKey(), httpRequest.getRemoteAddr());
			}
		}
		
		UserServiceAuthenticationResponse response = new UserServiceAuthenticationResponse();
		if (userSession != null){
			response.setEncUserSessionID(userSession.getSessionID());
			response.setEncExpiryTime(iDateUtil.generateStringFromDate(userSession.getExpiryTime()));
		}
		return response;
	}
	
}