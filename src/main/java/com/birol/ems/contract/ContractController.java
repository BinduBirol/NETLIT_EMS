package com.birol.ems.contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.repo.EmployeeRepository;
import com.birol.persistence.model.User;

@Controller
public class ContractController {
	
	@Autowired
	EmployeeRepository employeeRepository;
	@Autowired
	EmailTemplateRepo emailTemplateRepo;
	
	@GetMapping("/newDocumentHome")
	public ModelAndView newDocumentHome(Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();
		ArrayList<EMPLOYEE_BASIC> all_emps= (ArrayList<EMPLOYEE_BASIC>) employeeRepository.findAll();
		ArrayList<Email_msg_DTO> email_templates=  emailTemplateRepo.findByEmpid(0);
		ArrayList<Email_msg_DTO> email_templates_my= emailTemplateRepo.findByEmpid(user.getId());
		email_templates.addAll(email_templates_my);
		model.addAttribute("emps", all_emps);
		model.addAttribute("email_templates", email_templates);
		return new ModelAndView("ems/pages/contract/newDocumentHome", model);
	}	
	
	@GetMapping("/emailTemplatesHome")
	public ModelAndView emailTemplatesHome(Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();
		ArrayList<Email_msg_DTO> tmlt= (ArrayList<Email_msg_DTO>) emailTemplateRepo.findAll();
		model.addAttribute("templates", tmlt);
		model.addAttribute("myid", user.getId());
		return new ModelAndView("ems/pages/contract/emailTemplatesHome", model);
	}
	
	@PostMapping("/doEmailTemplate")
	public ModelAndView addEmailTemplate(Authentication auth, final ModelMap model,@ModelAttribute Email_msg_DTO template) {
		User user = (User) auth.getPrincipal();		
		emailTemplateRepo.save(template);
		model.addAttribute("msg", "Template saved successfuly.");
		return new ModelAndView("redirect:/emailTemplatesHome", model);
	}
	
	@PostMapping("/deleteEmailTemplate")
	public ModelAndView deleteEmailTemplate(Authentication auth, final ModelMap model,@Payload long id) {
		User user = (User) auth.getPrincipal();	
		emailTemplateRepo.deleteById(id);
		model.addAttribute("msg", "Template deleted successfuly.");
		return new ModelAndView("redirect:/emailTemplatesHome", model);
	}

}
