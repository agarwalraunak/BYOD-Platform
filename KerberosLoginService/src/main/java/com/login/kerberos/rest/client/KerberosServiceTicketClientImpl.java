/**
 * 
 */
package com.login.kerberos.rest.client;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.config.KerberosURLConfig;
import com.login.exception.RestClientException;
import com.login.kerberos.rest.api.IKerberosServiceRequestAPI;
import com.login.kerberos.rest.api.KerberosAuthenticationAPIImpl.ServiceTicketResponseAttributes;
import com.login.model.SessionDirectory;
import com.login.model.kerberos.KerberosAppSession;
import com.login.model.kerberos.ServiceTicket;
import com.login.model.kerberos.TGT;
import com.login.util.dateutil.IDateUtil;

/**
 * @author raunak
 *
 */
@Component
public class KerberosServiceTicketClientImpl implements IKerberosRequestServiceTicketClient{
	
	private static Logger log = Logger.getLogger(KerberosServiceTicketClientImpl.class);
	
	private @Autowired SessionDirectory sessionDirectory;
	private @Autowired IKerberosServiceRequestAPI iServiceRequestAPI;
	private @Autowired KerberosURLConfig kerberosURLConfig;
	private @Autowired IDateUtil iDateUtil;
	
	@Override
	public ServiceTicket getServiceTicketForApp(String serviceName, KerberosAppSession appSession) throws IOException, RestClientException  {
		
		log.debug("Entering getServiceTicketForApp method");
		
		if (serviceName == null || serviceName.isEmpty() || appSession == null){
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
		
		Map<ServiceTicketResponseAttributes, String> responseAttributes = iServiceRequestAPI.requestServiceTicketForApp(kerberosURLConfig.getKERBEROS_APP_SERVICE_TICKET_REQUEST_URL(), appTGT.getTgtPacket(), serviceName, appSession.getSessionID());
		
		if (responseAttributes == null){
			return null;
		}
		
		String serviceTicketPacket = responseAttributes.get(ServiceTicketResponseAttributes.SERVICE_TICKET_PACKET); 
		String serviceSessionID = responseAttributes.get(ServiceTicketResponseAttributes.SERVICE_SESSION_ID);
		String decServiceName = responseAttributes.get(ServiceTicketResponseAttributes.SERVICE_NAME);
		String expriyTimeStr = responseAttributes.get(ServiceTicketResponseAttributes.EXPIRY_TIME);
		
		Date expiryTime = iDateUtil.generateDateFromString(expriyTimeStr);
		
		serviceTicket = appTGT.createServiceTicket(serviceSessionID, serviceTicketPacket, decServiceName, expiryTime);
		
		return serviceTicket;
		
	}
}
