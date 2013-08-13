/**
 * 
 */
package com.device.kerberos.rest.client;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.management.InvalidAttributeValueException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.device.config.KerberosURLConfig;
import com.device.kerberos.model.KerberosAppSession;
import com.device.kerberos.model.KerberosSessionManager;
import com.device.kerberos.model.ServiceTicket;
import com.device.kerberos.model.TGT;
import com.device.kerberos.rest.api.IKerberosServiceRequestAPI;
import com.device.kerberos.rest.api.KerberosAppAuthenticationAPIImpl.ServiceTicketResponseAttributes;
import com.device.util.dateutil.IDateUtil;

/**
 * @author raunak
 *
 */
@Component
public class KerberosRequestServiceTicketClient {
	
	private static Logger log = Logger.getLogger(KerberosRequestServiceTicketClient.class);
	
	private @Autowired KerberosSessionManager kerberosSessionManager;
	private @Autowired IKerberosServiceRequestAPI iServiceRequestAPI;
	private @Autowired KerberosURLConfig kerberosURLConfig;
	private @Autowired IDateUtil iDateUtil;

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
		
		ServiceTicket serviceTicket = appTGT.findActiveServiceTicketByServiceName(serviceName);
		if (serviceTicket != null){
			return serviceTicket;
		}
		
		Map<ServiceTicketResponseAttributes, String> responseAttributes = null;
		try {
			responseAttributes = iServiceRequestAPI.requestServiceTicketForApp(kerberosURLConfig.getKERBEROS_APP_SERVICE_TICKET_REQUEST_URL(), appTGT.getTgtPacket(), serviceName, appSession.getSessionID());
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
		String expiryTimeStr = responseAttributes.get(ServiceTicketResponseAttributes.EXPIRY_TIME);
		
		Date expiryTime = iDateUtil.generateDateFromString(expiryTimeStr);
		
		serviceTicket = appTGT.createServiceTicket(serviceSessionID, serviceTicketPacket, decServiceName, expiryTime);
		
		return serviceTicket;
		
	}
}
