/**
 * 
 */
package com.config.controller;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.management.InvalidAttributeValueException;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.config.forms.SecretKeyForm;
import com.kerberos.util.ActiveDirectory.ActiveDirectoryImpl.SecretKeyType;
import com.kerberos.util.keyserver.KeyServerUtil;

/**
 * @author raunak
 *
 */

@Controller
@RequestMapping("/keystore")
public class KeyManagerConfig {
	
	private @Autowired KeyServerUtil keyServerUtil;
	
	@RequestMapping("/key/new")
	public String newSecretKeyForm(ModelMap model) throws InvalidAttributeValueException, NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, CertificateException, NamingException, IOException{
		model.addAttribute("secretKeyForm", new SecretKeyForm());
		model.addAttribute("keyTypes", SecretKeyType.values());
		
		return "secretKeyForm";
	}

	@RequestMapping(value="/key/new", method=RequestMethod.POST)
	public String storeSecretKey(@ModelAttribute("secretKeyForm") SecretKeyForm secretKeyForm, ModelMap model){
		
		String uid = secretKeyForm.getAppUID();
		String keyType = secretKeyForm.getKeyType();
		
		if (keyType == null || keyType.isEmpty()){
			model.addAttribute("statusMessage", "Please select a key type");
			return "secretKeyForm";
		}
		
		try {
			keyServerUtil.saveKeyToKeyStore(uid, SecretKeyType.valueOf(keyType));
			model.addAttribute("statusMessage", "Registration Successfull");
		} catch (InvalidAttributeValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("statusMessage", "Registration failed");
		}
		
		return "secretKeyForm";
	}
}
