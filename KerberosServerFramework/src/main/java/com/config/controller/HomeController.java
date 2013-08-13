/**
 * 
 */
package com.config.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author raunak
 *
 */

@Controller
public class HomeController {

	@RequestMapping(value="/home")
	public String homePage(){
		return "home";
	}
	
}
