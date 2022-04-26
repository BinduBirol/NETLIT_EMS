package com.birol.ems.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.birol.security.ActiveUserStore;
import com.birol.service.IUserService;

@Controller
public class EMScontroller {
	@Autowired
    ActiveUserStore activeUserStore;
	@Autowired
	com.birol.security.LoggedUser loggedUser;

    @Autowired
    IUserService userService;
    
	@GetMapping("/dashboard")
	 public ModelAndView dashboard(final ModelMap model) {
		
		model.addAttribute("users", activeUserStore.getUsers());
		model.addAttribute("cuser", loggedUser.getUsername());;
		return new ModelAndView("homepage", model);		
	}

}
