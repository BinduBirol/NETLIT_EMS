package com.birol.ems.controller;

import java.util.Collection;

import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.birol.persistence.dao.RoleRepository;
import com.birol.persistence.model.Role;
import com.birol.persistence.model.User;

@Controller
public class AdminstarionController {
	@Autowired
	RoleRepository roleRepository;
	@GetMapping("/loginasHome")
	public ModelAndView loginasHome(final ModelMap model) throws LazyInitializationException {		
		model.addAttribute("roles", roleRepository.findAll());		
		return new ModelAndView("ems/pages/loginasHome", model);
	}
	@GetMapping("/getuserlistbyrole")
	public ModelAndView getuserlistbyrole(@RequestParam String role, final ModelMap model) throws LazyInitializationException {	
		
		Role roleobj= roleRepository.findByName(role);
		Collection<User> users= roleobj.getUsers();
		model.addAttribute("users",users);
		return new ModelAndView("ems/ajaxResponse/selectUsersByRole", model);
	}
	
}
