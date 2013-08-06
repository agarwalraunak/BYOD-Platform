package com.kerberos.test;

import javax.management.InvalidAttributeValueException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kerberos.encryption.IEncryptionUtil;

@Controller
public class TestController {

	private @Autowired IEncryptionUtil iEncryptionUtil;
	
	@RequestMapping("/")
	public void test(){
		try {
			iEncryptionUtil.generateSecretKey(null);
		} catch (InvalidAttributeValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
