/**
 * 
 */
package com.kerberos.rest.resource;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kerberos.configuration.KerberosConfigurationManager;
import com.kerberos.db.model.TGT;
import com.kerberos.db.service.ITGTService;
import com.kerberos.exceptions.InvalidInputException;
import com.kerberos.exceptions.InvalidOutputException;
import com.kerberos.rest.api.IKDCApi;
import com.kerberos.rest.api.KDCApiImpl.TicketAttributes;
import com.kerberos.rest.representation.AuthenticationResponse;
import com.kerberos.util.ActiveDirectory.IActiveDirectory;
import com.kerberos.util.dateutil.IDateUtil;
import com.kerberos.util.encryption.IEncryptionUtil;
import com.kerberos.util.hashing.IHashUtil;

/**
 * @author raunak
 *
 */
@Component
@Path("/kdc")
public class KDCRestService {
	
	private static Logger log = Logger.getLogger(KDCRestService.class);
	
	private @Autowired IActiveDirectory activeDirectory;
	
	private @Autowired IKDCApi iKDCApi;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	private @Autowired ITGTService iTGTService;
	private @Autowired KerberosConfigurationManager kerberosConfigurationManager;
	private @Autowired IHashUtil iHashUtil;
	/**
	 * @param username
	 * @return
	 */
	@GET
	@Path("/authenticate/app/{username}")
	@Consumes(MediaType.TEXT_HTML)
	@Produces(MediaType.APPLICATION_JSON)
	public AuthenticationResponse authenticateApp(@PathParam("username") String username) {
		
		log.debug("Entring the authenticateApp method");
		
		if (username == null || username.isEmpty()){
			log.error("Invalid username found!");
			throw new InvalidInputException("Username is not valid, can not be null or empty!", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML);
		}
		
		String password = null;
		try {
			password = iKDCApi.fetchPassworkFromDirectoryForUsername(username);
		} catch (InvalidAttributeValueException e) {
			log.error("Request from unregistered user found");
			e.printStackTrace();
			throw new InvalidOutputException("Invalid username", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML);
		}
		if (password == null || password.isEmpty()){
			throw new InvalidOutputException("Invalid username", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML);
		}

		SecretKey passwordKey = null;
		try {
			passwordKey = iKDCApi.generatePasswordSymmetricKey(username, password);
		} catch (InvalidAttributeValueException | NoSuchAlgorithmException e) {
			log.error("Error generating password key \n"+e.getMessage());
			e.printStackTrace();
			throw new InvalidOutputException("Error processing request. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		if (passwordKey == null){
			throw new InvalidOutputException("Error processing request. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		Map<TicketAttributes, String> responseAttributes = null;
		boolean tgtIsExisting = false;
		
		//Check if TGT exists for the User
		TGT tgt = iTGTService.findActiveTGTForUsername(username);
		if (tgt != null && iKDCApi.checkIfTGTIsValid(tgt)){
			tgtIsExisting = true;
				try {
					responseAttributes = iKDCApi.createResponseAttrbiutes(username, tgt.getExpiresOn(), tgt.getSessionKey());
				} catch (InvalidAttributeValueException e) {
					log.error("Error creating response attributes with existing TGT \n"+e.getMessage());
					e.printStackTrace();
				}
				if (responseAttributes == null){
					throw new InvalidOutputException("Error processing request. Please try again after sometime", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
				}
		}
		//Else create response attributes using a new TGT
		else{
			try {
				responseAttributes = iKDCApi.createResponseAttrbiutes(username, iDateUtil.generateDateWithDelay(kerberosConfigurationManager.getTGT_TIME_OUT()), iHashUtil.getSessionKey());
			} catch (InvalidAttributeValueException  e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
			if (responseAttributes == null){
				throw new InvalidOutputException("Error processing request. Please try again after sometime", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
			}
		}
		
		String ticket = responseAttributes.get(TicketAttributes.TICKET);
		String tgtExpiryTime = responseAttributes.get(TicketAttributes.TGT_EXPIRY_TIME);
		String sessionKey = responseAttributes.get(TicketAttributes.SESSION_KEY);
		
		String[] encAttributes;
		AuthenticationResponse response = null;
		try {
			encAttributes = iEncryptionUtil.encrypt(passwordKey, username, sessionKey, ticket);
			response = iKDCApi.createAuthenticationResponse(encAttributes[0], encAttributes[1], encAttributes[2]);
			
			//If TGT does not exist save the new one
			if (!tgtIsExisting)
				iKDCApi.createTGT(username, sessionKey, iDateUtil.generateDateFromString(tgtExpiryTime));
		} catch (InvalidAttributeValueException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		if (response == null){
			throw new InvalidOutputException("Error processing request. Please try again after sometime", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		log.debug("Returning from authenticateApp");
		
		return response;
	}
}
