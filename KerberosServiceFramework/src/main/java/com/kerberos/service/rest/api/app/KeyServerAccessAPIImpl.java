package com.kerberos.service.rest.api.app;

import javax.management.InvalidAttributeValueException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kerberos.service.models.KerberosAppSession;
import com.kerberos.service.models.KerberosSessionManager;
import com.kerberos.service.models.ServiceTicket;
import com.kerberos.service.models.TGT;
import com.kerberos.service.rest.api.kerberos.IKerberosAuthenticationAPI;
import com.kerberos.service.rest.api.kerberos.KerberosAuthenticationAPIImpl.SecretKeyType;
import com.kerberos.service.rest.client.KerberosAuthenticationClient;
import com.kerberos.service.rest.client.KerberosServiceTicketClient;
import com.kerberos.service.rest.exceptions.ServiceUnavailableException;
import com.kerberos.service.util.dateutil.IDateUtil;
import com.kerberos.service.util.encryption.IEncryptionUtil;

public class KeyServerAccessAPIImpl implements IKeyServerAccessAPI {
	
	private static Logger log = Logger.getLogger(KeyServerAccessAPIImpl.class);
	
	private @Autowired KerberosSessionManager kerberosSessionManager;
	private @Autowired KerberosAuthenticationClient kerberosAuthenticationClient;
	private @Autowired KerberosServiceTicketClient kerberosServiceTicketClient;
	private @Autowired IKerberosAuthenticationAPI iAuthenticationAPI;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IDateUtil iDateUtil;
	
	
	@Override
	public ServiceTicket getKeyServerServiceTicketForApp() throws ServiceUnavailableException{
		
		log.debug("Entering getKeyServerServiceTicketForApp method");
		
		KerberosAppSession appSession = kerberosSessionManager.getAppSession();
		ServiceTicket serviceTicket = null;
		if (appSession == null){
			try {
				if (!kerberosAuthenticationClient.kerberosAuthentication()){
					log.error("Unable to authenticate the app");
					throw new ServiceUnavailableException("Error processing the request. Please try again later!", Response.Status.SERVICE_UNAVAILABLE, MediaType.TEXT_HTML);
				}
			} catch (InvalidAttributeValueException e) {
				log.error("Unable to authenticate the app. Detailed exception is attached: "+e.getMessage());
				e.printStackTrace();
				throw new ServiceUnavailableException("Error processing the request. Please try again later!", Response.Status.SERVICE_UNAVAILABLE, MediaType.TEXT_HTML);
			}
			
			appSession = kerberosSessionManager.getAppSession();
			if (appSession == null){
				log.error("Error processing the request, Authenticated Application session does not exist");
				throw new ServiceUnavailableException("Error processing the request. Please try again later!", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
			}
		}
			
		TGT tgt = appSession.getTgt();
		if (tgt == null){
			log.error("Error processing the request, Authenticated Application session does not exist");
			throw new ServiceUnavailableException("Error processing the request. Please try again later!", Response.Status.INTERNAL_SERVER_ERROR, MediaType.TEXT_HTML);
		}
		
		serviceTicket = tgt.findServiceTicketByServiceName(SecretKeyType.KEY_SERVER.getValue());
		if (serviceTicket == null){
			serviceTicket = kerberosServiceTicketClient.getServiceTicketForApp(SecretKeyType.KEY_SERVER.getValue());
			if (serviceTicket == null){
				log.error("Error processing the request, failed to get the Key Server Service Ticket for Application");
				throw new ServiceUnavailableException("Error processing the request. Please try again later!", Response.Status.SERVICE_UNAVAILABLE, MediaType.TEXT_HTML);
			}
		}
		
		log.debug("Returning from getKeyServerServiceTicketForApp method");

		return serviceTicket;
	}

}
