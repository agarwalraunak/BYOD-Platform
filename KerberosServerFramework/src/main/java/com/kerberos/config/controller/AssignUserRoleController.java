/**
 * 
 */
package com.kerberos.config.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kerberos.config.forms.UserRoleForm;
import com.kerberos.util.ActiveDirectory.IActiveDirectory;
import com.kerberos.util.encryption.IEncryptionUtil;

/**
 * @author raunak
 *
 */

@Controller
@RequestMapping(value="/assign/user/role")
public class AssignUserRoleController {
	
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired IActiveDirectory iActiveDirectory;

	@RequestMapping(method=RequestMethod.GET)
	public String assignUserRoleForm(ModelMap model){

		UserRoleForm form = new UserRoleForm();
		model.addAttribute("userRoleForm", form);
		return "userRoleForm";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String assignUserRole(@ModelAttribute("userRoleForm") UserRoleForm userRoleForm, ModelMap model){
		
		String appID = userRoleForm.getAppID();
		String userID = userRoleForm.getUserID();
		String title = userRoleForm.getTitle();
		String employeeType = userRoleForm.getEmployeeType();
		
		if (!iEncryptionUtil.validateDecryptedAttributes(appID, userID, title, employeeType)){
			model.addAttribute("statusMessage", "Invalid Input!");
			return "userRoleForm";
		}
		
		Map<String, String> userData = null;
		try {
			userData = iActiveDirectory.getDataByUID(userID, iActiveDirectory.getUSER_DIRECTORY_CONTEXT(), new String[]{"cn", "sn"});
		} catch (IOException | NamingException e) {
			e.printStackTrace();
			model.addAttribute("statusMessage", "User does not exist!");
			return "userRoleForm";
		}
		
		if (userData.size() == 0){
			model.addAttribute("statusMessage", "User does not exist!");
			return "userRoleForm";
		}
		
		String cn = userData.get("cn");
		String sn = userData.get("sn");
		Map<String, String> data = new HashMap<>();
		
		data.put("title", title);
		data.put("employeeType", employeeType);
		
		try {
			iActiveDirectory.addUserToApplication(userID, cn, sn, appID, data);
		} catch (IOException | NamingException e) {
			e.printStackTrace();
			model.addAttribute("statusMessage", "Error processing request!");
			return "userRoleForm";
		}
		
		model.addAttribute("statusMessage", "Process Successfull");
		return "userRoleForm";
	}
	
	
}
