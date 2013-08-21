/**
 * 
 */
package com.device.demo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.device.kerberos.model.KerberosAppSession;
import com.device.kerberos.model.KerberosSessionManager;
import com.device.kerberos.model.ServiceTicket;
import com.device.kerberos.model.TGT;
import com.device.service.model.UserSession;

/**
 * @author raunak
 *
 */
@Controller
public class DemoSessionManagementController {
	
	private @Autowired KerberosSessionManager sessionDirectory;

	@RequestMapping(value="/demo/session/management")
	public String showSessionManagementData(ModelMap model){
		
		KerberosAppSession kerberosAppSession = sessionDirectory.getKerberosAppSession();
		
		TGT tgt = kerberosAppSession.getTgt();
		Map<String, ServiceTicket> serviceTickets = tgt.getServiceTickets();
		
		model.addAttribute("kerberosAppSession", kerberosAppSession);
		model.addAttribute("serviceTickets", serviceTickets.values());
		
		return "sessionManagementDemo";
	}
	
	@RequestMapping("/demo/session/user")
	public String showUserSessions(@RequestParam("serviceSessionID") String serviceSessionID, ModelMap model){
		
		String id = null;
		if (serviceSessionID != null && !serviceSessionID.isEmpty())
			id = serviceSessionID.subSequence(1, serviceSessionID.length()-1).toString();
		KerberosAppSession kerberosAppSession = sessionDirectory.getKerberosAppSession();
		TGT tgt = kerberosAppSession.getTgt();
		ServiceTicket serviceTicket = tgt.findServiceTicketByServiceSessionID(id);
		
		List<UserSession> userSessionList = serviceTicket.getAppSession().getUserSessions();
		
		model.addAttribute("userSessionList", userSessionList);
		
		return "userSessionManagementDAO";
	}
	
}
