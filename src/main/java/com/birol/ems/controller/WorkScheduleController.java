package com.birol.ems.controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.repo.EmployeeRepository;
import com.birol.ems.service.EmployeeService;

@Controller
public class WorkScheduleController {
	@Autowired
	EmployeeService employeeService;
	@Autowired
	EmployeeRepository employeeRepository;
	
	@GetMapping("/work_schedule_home")
	public ModelAndView work_schedule_home(final ModelMap model) {		
		model.addAttribute("employees", employeeService.getEmployeeList());
		return new ModelAndView("ems/pages/work_schedule_home", model);
	}

	@GetMapping("/workschedule")
	public ModelAndView workschedule(@RequestParam("empid") Long empid, final ModelMap model) {
		EMPLOYEE_BASIC empdtl = employeeRepository.findbyEmpid(empid);
		if (empdtl.getEmp_image() != null) {
			String imageencode = Base64.getEncoder().encodeToString(empdtl.getEmp_image());
			empdtl.setEmp_image_encoded(imageencode);
		}
		// model.addAttribute("user",user);
		model.addAttribute("userdtl", empdtl);
		// return new ModelAndView("ems/pages/profile", model);
		return new ModelAndView("ems/ajaxResponse/viewworkschedule", model);
	}
	
	@GetMapping("/workschedulehistory")
	public ModelAndView workschedulehistory(@RequestParam("empid") Long empid, final ModelMap model) {
		EMPLOYEE_BASIC empdtl = employeeRepository.findbyEmpid(empid);
		if (empdtl.getEmp_image() != null) {
			String imageencode = Base64.getEncoder().encodeToString(empdtl.getEmp_image());
			empdtl.setEmp_image_encoded(imageencode);
		}
		model.addAttribute("employees", employeeService.getEmployeeList());
		model.addAttribute("emp", empdtl);
		return new ModelAndView("ems/ajaxResponse/viewWorkShHistory", model);
	}
}
