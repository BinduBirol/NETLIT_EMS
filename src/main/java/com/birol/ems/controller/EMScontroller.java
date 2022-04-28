package com.birol.ems.controller;

import java.security.Principal;

import javax.servlet.http.HttpSession;

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
    
	@GetMapping("/dashboard")
	 public ModelAndView dashboard(final ModelMap model, Authentication auth) {		
		model.addAttribute("loggedInUsers", activeUserStore.getUsers());	
		User user = (User)auth.getPrincipal();		
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
