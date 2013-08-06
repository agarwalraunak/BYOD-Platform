/**
 * 
 */
package com.kerberos.service.rest.client;

import java.io.IOException;
import java.util.Map;

import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kerberos.service.models.KerberosAppSession;
import com.kerberos.service.models.KerberosSessionManager;
import com.kerberos.service.models.ServiceTicket;
import com.kerberos.service.models.TGT;
import com.kerberos.service.rest.api.kerberos.IKerberosServiceRequestAPI;
import com.kerberos.service.rest.api.kerberos.KerberosAuthenticationAPIImpl.ServiceTicketResponseAttributes;

/**
 * @author raunak
 *
 */
public class KerberosServiceTicketClient {
	
	private static Logger log = Logger.getLogger(KerberosServiceTicketClient.class);
	
	private @Autowired KerberosSessionManager kerberosSessionManager;
	private @Autowired IKerberosServiceRequestAPI iServiceRequestAPI;
	private static final String APP_SERVICE_TICKET_REQUEST_URL = "http://localhost:8080/kerberos/apple/TGS/request/serviceTicket/";
	
	public ServiceTicket getServiceTicketForApp(String serviceName) {
		
		log.debug("Entering getServiceTicketForApp method");
		
		if (serviceName == null){
			return null;
		}
		
		KerberosAppSession appSession = kerberosSessionManager.getAppSession();
		if (appSession == null){
			return null;
		}
		
		TGT appTGT = appSession.getTgt();
		if (appTGT == null){
			return null;
		}
		
		ServiceTicket serviceTicket = appTGT.findServiceTicketByServiceName(serviceName);
		if (serviceTicket != null){
			return serviceTicket;
		}
		
		Map<ServiceTicketResponseAttributes, String> responseAttributes = null;
		try {
			responseAttributes = iServiceRequestAPI.requestServiceTicketForApp(APP_SERVICE_TICKET_REQUEST_URL, appTGT.getTgtPacket(), serviceName, appSession.getSessionID());
		} catch (IOException | InvalidAttributeValueException e) {
			e.printStackTrace();
			return null;
		}
		
		if (responseAttributes == null){
			return null;
		}
		
		String serviceTicketPacket = responseAttributes.get(ServiceTicketResponseAttributes.SERVICE_TICKET_PACKET); 
		String serviceSessionID = responseAttributes.get(ServiceTicketResponseAttributes.SERVICE_SESSION_ID);
		String decServiceName = responseAttributes.get(ServiceTicketResponseAttributes.SERVICE_NAME);
		
		serviceTicket = appTGT.createServiceTicket(serviceSessionID, serviceTicketPacket, decServiceName);
		
		return serviceTicket;
		
	}
}
