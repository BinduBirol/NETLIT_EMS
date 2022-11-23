package com.birol.ems.contract;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.repo.EmployeeRepository;
import com.birol.persistence.model.User;

@Controller
public class ContractController {
	
	@Autowired
	EmployeeRepository employeeRepository;
	
	@GetMapping("/newDocumentHome")
	public ModelAndView newDocumentHome(Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();
		ArrayList<EMPLOYEE_BASIC> all_emps= (ArrayList<EMPLOYEE_BASIC>) employeeRepository.findAll();
		model.addAttribute("emps", all_emps);
		return new ModelAndView("ems/pages/contract/newDocumentHome", model);
	}

}
