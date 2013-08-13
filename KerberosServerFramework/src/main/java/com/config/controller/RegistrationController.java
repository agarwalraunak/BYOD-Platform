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
import com.kerberos.util.ActiveDirectory.EntryDetails;
import com.kerberos.util.ActiveDirectory.IActiveDirectory;
import com.kerberos.util.encryption.IEncryptionUtil;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
	
	private @Autowired IActiveDirectory apacheDSUtil;
	private @Autowired IEncryptionUtil iEncryptionUtil;
	
	@RequestMapping("/form")
	public String registrationForm(ModelMap model){
		model.addAttribute("registrationForm", new RegistrationForm());
		
		return "registrationForm";
	}
	
	@RequestMapping(value="/form", method=RequestMethod.POST)
	public String createNewEntry(@ModelAttribute("registrationForm") RegistrationForm form, ModelMap model){
		
		String commonName = form.getCommonName();
		String surname = form.getSurName();
		String username = form.getUid();
		String password = form.getPassword();
		String isApplication = form.getIsApplication();
		
		if (!iEncryptionUtil.validateDecryptedAttributes(commonName, surname, username, password, isApplication)){
			model.addAttribute("statusMessage", "Invalid Input");
			return "registrationForm";
		}
		
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
			model.addAttribute("statusMessage", "Registration Successfull");
		} catch (IOException | NamingException e) {
			e.printStackTrace();
			model.addAttribute("statusMessage", "Registration Failed");
		}
		
		return "registrationForm";
	}
}
