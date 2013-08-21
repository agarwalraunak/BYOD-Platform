/**
 * 
 */
package com.service.demo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.service.model.SessionDirectory;
import com.service.model.app.AppSession;
import com.service.model.kerberos.KerberosAppSession;
import com.service.model.kerberos.ServiceTicket;
import com.service.model.kerberos.TGT;

/**
 * @author raunak
 *
 */
@Controller
public class DemoSessionManagementController {
	
	private @Autowired SessionDirectory sessionDirectory;

	@RequestMapping(value="/demo/session/management")
	public String showSessionManagementData(ModelMap model){
		
		KerberosAppSession kerberosAppSession = sessionDirectory.getKerberosAppSession();
		
		TGT tgt = kerberosAppSession.getTgt();
		Map<String, ServiceTicket> serviceTickets = tgt.getServiceTickets();
		
		Map<String, AppSession> appSessionDirectory = sessionDirectory.getAppSessionDirectory();
		
		model.addAttribute("kerberosAppSession", kerberosAppSession);
		model.addAttribute("serviceTickets", serviceTickets);
		model.addAttribute("appSessionDir", appSessionDirectory.values());
		
		
		return "sessionManagementDemo";
	}
	
}
