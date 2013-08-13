package com.kerberos.rest.resource;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Map;

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

import com.kerberos.configuration.KerberosConfigurationManager;
import com.kerberos.db.model.ServiceTicket;
import com.kerberos.db.model.TGT;
import com.kerberos.db.service.IServiceTicketService;
import com.kerberos.db.service.ITGTService;
import com.kerberos.exceptions.InvalidInputException;
import com.kerberos.exceptions.InvalidOutputException;
import com.kerberos.exceptions.ServiceUnavailableException;
import com.kerberos.rest.api.ITGSApi;
import com.kerberos.rest.api.TGSApiImpl.ServiceTicketRequestAttributes;
import com.kerberos.rest.representation.ServiceTicketRequest;
import com.kerberos.rest.representation.ServiceTicketResponse;
import com.kerberos.util.ActiveDirectory.ActiveDirectoryImpl.SecretKeyType;
import com.kerberos.util.dateutil.IDateUtil;
import com.kerberos.util.encryption.IEncryptionUtil;
import com.kerberos.util.hashing.IHashUtil;
import com.kerberos.util.keyserver.KeyServerUtil;

@Component
@Path("/TGS")
public class TGSRestService {
	
	private static Logger log = Logger.getLogger(TGSRestService.class);

	private @Autowired KeyServerUtil keyServerUtil;
	private @Autowired ITGSApi iTGSApi;
	private @Autowired ITGTService iTGTService;
	private @Autowired IHashUtil iHashUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	private @Autowired KerberosConfigurationManager kerberosConfigurationManager;
	private @Autowired IServiceTicketService iServiceTicketService;
	
	@POST
	@Path("/request/serviceTicket/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ServiceTicketResponse ticketGrantingService(ServiceTicketRequest request){

		log.debug("Entering ticketGrantingService method");
		
		ServiceTicket serviceTicket = null;
		SecretKey kdcMasterKey;
		try {
			kdcMasterKey = keyServerUtil.getKeyFromKeyStore(null, SecretKeyType.KDC_MASTER_KEY);
		} catch (InvalidAttributeValueException | NoSuchAlgorithmException| UnrecoverableEntryException | KeyStoreException| CertificateException | NamingException | IOException e1) {
			log.error("Failed to retrieve key from key server. Detail exception attached below:");
			e1.printStackTrace();
			throw new ServiceUnavailableException("Error processing request, service unavailable. Please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		Map<ServiceTicketRequestAttributes, String> requestAttributes = null;
		try {
			requestAttributes = iTGSApi.getServiceTicketRequestAttributes(request.getEncAppTgtPacket(), request.getEncAuthenticator(), request.getEncServiceName(), kdcMasterKey);
		} catch (InvalidAttributeValueException e) {
			log.error("Invalid Input parameters to getServiceTicketRequestAttributes. Detail exception attached below:");
			e.printStackTrace();
			throw new InvalidInputException("Invalid TGT!", Response.Status.FORBIDDEN, MediaType.TEXT_HTML);
		}
		if (requestAttributes == null){
			throw new InvalidOutputException("Error processing request, Bad request found", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML);
		}
		
		String sessionKey = requestAttributes.get(ServiceTicketRequestAttributes.SESSION_KEY);
		String username = requestAttributes.get(ServiceTicketRequestAttributes.USERNAME);
		String serviceName = requestAttributes.get(ServiceTicketRequestAttributes.SERVICE_NAME);
		String requestAuthenticatorString = requestAttributes.get(ServiceTicketRequestAttributes.REQUEST_AUTHENTICATOR);
		
		TGT tgt = iTGTService.findTGTForSessionKey(sessionKey);
		
		//Find Active Service Ticket for the given TGT
		serviceTicket = iServiceTicketService.findActiveServiceTicketByTGTAndServiceName(tgt, serviceName);
		
		SecretKey sessionSecretKey = null;
		try {
			sessionSecretKey = iEncryptionUtil.generateSecretKey(sessionKey);
		} catch (InvalidAttributeValueException e) {
			log.error("Error generating secret key from session key. Request won't be processed further. Detail exception attached below:\n"+e.getMessage());
			e.printStackTrace();
			throw new InvalidOutputException("Error processing request, please try again later", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML);
		}
		if (sessionSecretKey == null){
			throw new InvalidOutputException("Error processing request, please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		
		SecretKey serviceKey = null;
		try {
			serviceKey = iTGSApi.getSecretKeyForServiceName(serviceName);
		} catch (InvalidAttributeValueException e) {
			log.error("Error fetching key from KeyServer. Detailed exception is attached below:\n"+e.getMessage());
			e.printStackTrace();
			throw new InvalidOutputException("Error processing request, broken request found", Response.Status.BAD_REQUEST, MediaType.TEXT_HTML);
		}
		if (serviceKey == null){
			throw new InvalidOutputException("Error processing request, please try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);			
		}
		
		
		Date requestAuthenticator = iDateUtil.generateDateFromString(requestAuthenticatorString);
		String serviceTicketExpiryString = null;
		String serviceSessionKey = null;
		
		//If service ticket exists don't create the new one
		if (serviceTicket != null){
			serviceTicketExpiryString = iDateUtil.generateStringFromDate(serviceTicket.getExpiresOn());
			serviceSessionKey = serviceTicket.getIdentifier();
		}
		else{
			//Generating the Service Ticket Expiration Time
			Date serviceTicketExpirationTime = iDateUtil.generateDateWithDelay(kerberosConfigurationManager.getSERVICE_TICKET_TIME_OUT());
			serviceTicketExpiryString = iDateUtil.generateStringFromDate(serviceTicketExpirationTime);
					
			serviceSessionKey = iHashUtil.getSessionKey();
			
			iServiceTicketService.saveServiceTicket(serviceSessionKey, tgt, serviceName, serviceTicketExpirationTime);
		}
		
		ServiceTicketResponse response;
		try {
			response = iTGSApi.createServiceTicketResponse(username, serviceName, serviceSessionKey, 
					serviceTicketExpiryString, requestAuthenticator, serviceKey, sessionSecretKey);
		} catch (InvalidAttributeValueException e) {
			e.printStackTrace();
			throw new InvalidInputException("Error processing request, try again later", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		log.debug("Returning from ticketGrantingService");
		
		return response;
	}
	
}
