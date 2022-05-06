package com.birol.ems.controller;

import java.io.File;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.birol.persistence.model.User;
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
	
	private static final Logger logger = LoggerFactory.getLogger(EMScontroller.class);
	  
    
	@GetMapping("/dashboard")
	 public ModelAndView dashboard(final ModelMap model, Authentication auth) {		
		model.addAttribute("loggedInUsers", activeUserStore.getUsers());	
		List<String> getUsersFromSessionRegistry= userService.getUsersFromSessionRegistry();
        model.addAttribute("getUsersFromSessionRegistry", getUsersFromSessionRegistry);
        List<User> getAlluser= userService.findAllUser();
        model.addAttribute("getAlluser", getAlluser);
        
		User user = (User)auth.getPrincipal();
		logger.error("error testing dashboard");
		return new ModelAndView("homepage", model);		
	}
	
	@GetMapping("/calendar")
	 public ModelAndView calander(final ModelMap model) {		
		return new ModelAndView("ems/pages/calendar", model);		
	}
	
	@GetMapping("/addEmployee")
	 public ModelAndView addEmployee(final ModelMap model) {		
		return new ModelAndView("ems/pages/addEmployee", model);		
	}
	
	@GetMapping("/employeeList")
	 public ModelAndView employeeList(final ModelMap model) {		
		return new ModelAndView("ems/pages/employeeList", model);		
	}

	@GetMapping("/changePassword")
	 public ModelAndView changePassword(final ModelMap model) {		
		return new ModelAndView("ems/pages/changePassword", model);		
	}
	
	@GetMapping("/profile")
	 public ModelAndView profile(final ModelMap model, Authentication auth) {
		User user = (User)auth.getPrincipal();
		model.addAttribute("user",user);
		return new ModelAndView("ems/pages/profile", model);		
	}
	
	@GetMapping("/settings")
	 public ModelAndView settings(final ModelMap model) {		
		return new ModelAndView("ems/pages/setting", model);		
	}
}
