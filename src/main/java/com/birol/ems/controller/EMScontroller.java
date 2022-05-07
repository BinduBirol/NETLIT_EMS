package com.birol.ems.controller;

import java.io.File;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.repo.EmployeeRepository;
import com.birol.ems.service.EmployeeService;
import com.birol.persistence.dao.RoleRepository;
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
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	EmployeeRepository employeeRepository;
	@Autowired
	EmployeeService employeeService;
	
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
		model.addAttribute("roles",roleRepository.findAll());
		
		return new ModelAndView("ems/pages/addEmployee", model);		
	}
	@PostMapping("/addEmployeeDo")	
	 public ModelAndView addEmployeeDo(@ModelAttribute EMPLOYEE_BASIC emp,ModelMap model, Authentication auth) {
		//model.addAttribute("roles",roleRepository.findAll());
		try {
			if(emp.getEmp_image_m().getSize()>0) {
				emp.setEmp_image(emp.getEmp_image_m().getBytes());
			}
			if(emp.getDoc_m_cv().getSize()>0) {
				emp.setDoc_cv(emp.getDoc_m_cv().getBytes());
			}
			if(emp.getDoc_m_crt().getSize()>0) {
				emp.setDoc_certificate(emp.getDoc_m_crt().getBytes());
			}
			if(emp.getDoc_m_id().getSize()>0) {
				emp.setDoc_id(emp.getDoc_m_id().getBytes());
			}
			User empCreator = (User)auth.getPrincipal();
			emp.setAdded_by(empCreator.getEmail());
			employeeRepository.save(emp);
		}catch (Exception e) {			
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		return new ModelAndView("redirect:/addEmployee", model);		
	}
	
	@GetMapping("/employeeList")
	 public ModelAndView employeeList(final ModelMap model) {
		model.addAttribute("employees",employeeService.getEmployeeList());
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
