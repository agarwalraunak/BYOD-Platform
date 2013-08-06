/**
 * 
 */
package com.kerberos.rest.resource;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.SecretKey;
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

import com.kerberos.ActiveDirectory.ActiveDirectoryImpl.SecretKeyType;
import com.kerberos.encryption.IEncryptionUtil;
import com.kerberos.exceptions.InvalidInputException;
import com.kerberos.exceptions.InvalidOutputException;
import com.kerberos.exceptions.ServiceUnavailableException;
import com.kerberos.keyserver.KeyServerUtil;
import com.kerberos.rest.api.IAccessServiceAPI;
import com.kerberos.rest.representation.AccessServiceResponse;
import com.kerberos.rest.representation.KeyServerRequest;

/**
 * @author raunak
 *
 */
@Component
@Path("/keyserver")
public class KeyServerService {
	
	private static Logger log = Logger.getLogger(KeyServerService.class);
	
	private @Autowired KeyServerUtil keyServerUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IAccessServiceAPI iAccessServiceAPI;
	
	@Path("/key")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AccessServiceResponse retrieveKeyFromKeyServer(KeyServerRequest request){
		
		log.debug("Entering retrieveKeyFromKeyServer method");
		
		SecretKey keyServerKey;
		try {
			keyServerKey = keyServerUtil.getKeyFromKeyStore(null, SecretKeyType.KEY_SERVER);
		} catch (InvalidAttributeValueException e){
			log.error("Failed to fetch the key from the key server. Detailed Exception is attached: "+e.getMessage());
			e.printStackTrace();
			throw new InvalidInputException("Error processing request. Bad Request found", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML);
		}  
		catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException | CertificateException | NamingException | IOException e) {
			log.error("Failed to retrive Key Server key. Detailed exception is attached below: \n "+e.getMessage());
			e.printStackTrace();
			throw new ServiceUnavailableException("Error processing request. Please try again later!", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		AccessServiceResponse response;
		try {
			response = iAccessServiceAPI.processKeyServerRequest(request, keyServerKey);
		} catch (InvalidAttributeValueException e) {
			log.error("Unable to process the request. Detailed exception is attached below: \n "+e.getMessage());
			e.printStackTrace();
			throw new InvalidOutputException("Error processing request. Bad request found", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML);
		}
		
		if (response == null){
			throw new InvalidOutputException("Error processing request. Bad request found", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML);
		}
		
		return response;
	}

}
