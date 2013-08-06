package com.config.controller;

import java.io.IOException;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.config.forms.RegistrationForm;
import com.kerberos.ActiveDirectory.EntryDetails;
import com.kerberos.ActiveDirectory.IActiveDirectory;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
	
	private @Autowired IActiveDirectory apacheDSUtil;
	
	@RequestMapping("/form")
	public String userRegistrationForm(ModelMap model){
		model.addAttribute("registrationForm", new RegistrationForm());
		
		return "registrationForm";
	}
	
	@RequestMapping(value="/form", method=RequestMethod.POST)
	public void createNewUser(@ModelAttribute("registrationForm") RegistrationForm form){
		
		String commonName = form.getCommonName();
		String surname = form.getSurName();
		String username = form.getUid();
		String password = form.getPassword();
		String isApplication = form.getIsApplication();
		
		EntryDetails details = new EntryDetails();
		details.setCommonName(commonName);
		details.setSurName(surname);
		details.setUserPassword(password);
		details.setUid(username);
		
		try {
			if (isApplication.equals("true"))
				apacheDSUtil.registerApp(details);
			else{
				apacheDSUtil.registerUser(details);
			}
		} catch (IOException | NamingException e) {
			e.printStackTrace();
		}
	}

}
