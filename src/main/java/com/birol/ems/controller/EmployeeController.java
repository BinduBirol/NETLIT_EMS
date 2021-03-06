package com.birol.ems.controller;

import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.birol.ems.dao.ComplaintsRepo;
import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.repo.EmployeeRepository;
import com.birol.ems.service.EmployeeService;
import com.birol.persistence.dao.RoleRepository;
import com.birol.persistence.model.Role;
import com.birol.persistence.model.User;
import com.birol.security.ActiveUserStore;
import com.birol.service.IUserService;

@Controller
public class EmployeeController {
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
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private Environment env;
	@Autowired
	ComplaintsRepo complaintsRepo;
	@Autowired
	com.birol.ems.dao.CommentRepo commentRepo;

	private static final Logger logger = LoggerFactory.getLogger(EMScontroller.class);

	@GetMapping("/addEmployee")
	public ModelAndView addEmployee(final ModelMap model) {
		model.addAttribute("roles", roleRepository.findAll());
		List<Integer> roles = new ArrayList<Integer>();
		roles.add(4);
		roles.add(6);
		roles.add(7);
		model.addAttribute("chief", employeeRepository.findbyroles(roles));
		return new ModelAndView("ems/pages/addEmployee", model);
	}

	@RequestMapping(value = "/addEmployeeDo", method = RequestMethod.POST)
	public ModelAndView addEmployeeDo(@ModelAttribute EMPLOYEE_BASIC emp, ModelMap model, Authentication auth,
			final HttpServletRequest request) {
		try {
			// check for mail existence
			EMPLOYEE_BASIC checkEmp = employeeRepository.findbyWorkMail(emp.getEmail());
			if (checkEmp != null) {
				model.addAttribute("message", "Already exists an employee with email: " + emp.getEmail()
						+ "\nPlease try another work email.");
				return new ModelAndView("theme/ajaxResponse", model);
			}

			if (emp.getEmp_image_m().getSize() > 0)
				emp.setEmp_image(emp.getEmp_image_m().getBytes());
			if (emp.getDoc_m_cv().getSize() > 0)
				emp.setDoc_cv(emp.getDoc_m_cv().getBytes());
			if (emp.getDoc_m_crt().getSize() > 0)
				emp.setDoc_certificate(emp.getDoc_m_crt().getBytes());
			if (emp.getDoc_m_id().getSize() > 0)
				emp.setDoc_id(emp.getDoc_m_id().getBytes());

			User empCreator = (User) auth.getPrincipal();
			emp.setAdded_by(empCreator.getEmail());
			employeeRepository.save(emp);
			// send mail
			SimpleMailMessage email = new SimpleMailMessage();
			email.setTo(emp.getEmail());
			email.setSubject("Information added");
			//final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort()
					//+ request.getContextPath();
			final String appUrl="https://ems.netlit.se";
			String emailText = "Dear " + emp.getFull_name()
					+ ",\nYour employment information succesfully added to NETLIT EMS system"
					+ "\nGo to the link to create your account:" + "\n" + appUrl + "/registration.html";
			email.setText(emailText);
			email.setFrom(env.getProperty("support.email"));
			mailSender.send(email);

			model.addAttribute("message", "Successfully Added Info For " + emp.getFull_name());
			model.addAttribute("class", "text-success");
		} catch (FileAlreadyExistsException fe) {
			fe.printStackTrace();
			model.addAttribute("message", fe.getMessage());
			logger.error(fe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", e.getMessage());
			logger.error(e.getMessage());
		}
		return new ModelAndView("theme/ajaxResponse", model);
	}

	@RequestMapping(value = "/editEmployeeDo", method = RequestMethod.POST)
	public ModelAndView editEmployeeDo(@ModelAttribute EMPLOYEE_BASIC emp, ModelMap model, Authentication auth,
			final HttpServletRequest request) {
		try {
			EMPLOYEE_BASIC empbasic = new EMPLOYEE_BASIC();
			empbasic = employeeRepository.findbyEmpid(emp.getEmpid());
			User user = userService.findUserByEmail(empbasic.getEmail());
			if (user != null) {
				Role role = roleRepository.getById((long) emp.getRoleid());
				user.setRoles(Arrays.asList(role));
				user.setUsing2FA(emp.isUsing2FA());
				userService.updateUserInfo(user);
			}
			emp.setStatus(empbasic.isStatus());
			emp.setCreated(empbasic.getCreated());
			try {
				if (emp.getEmp_image_m().getSize() > 0) {
					emp.setEmp_image(emp.getEmp_image_m().getBytes());
				} else {
					emp.setEmp_image(empbasic.getEmp_image());
				}
			} catch (Exception e) {
				model.addAttribute("message", e.getMessage());
				logger.error(e.getMessage());
			}
			// documents
			try {
				if (emp.getDoc_m_cv().getSize() > 0) {
					emp.setDoc_cv(emp.getDoc_m_cv().getBytes());
				} else {
					emp.setDoc_cv(empbasic.getDoc_cv());
				}
			} catch (Exception e) {
				model.addAttribute("message", e.getMessage());
				logger.error(e.getMessage());
			}
			try {
				if (emp.getDoc_m_crt().getSize() > 0) {
					emp.setDoc_certificate(emp.getDoc_m_crt().getBytes());
				} else {
					emp.setDoc_certificate(empbasic.getDoc_certificate());
				}
			} catch (Exception e) {
				model.addAttribute("message", e.getMessage());
				logger.error(e.getMessage());
			}
			try {
				if (emp.getDoc_m_id().getSize() == 0) {
					emp.setDoc_id(emp.getDoc_m_id().getBytes());
				} else {
					emp.setDoc_id(empbasic.getDoc_id());
				}
			} catch (Exception e) {
				model.addAttribute("message", e.getMessage());
				logger.error(e.getMessage());
			}
			try {
				if (emp.getDoc_m_others().getSize() == 0) {
					emp.setDoc_id(emp.getDoc_m_others().getBytes());
				} else {
					emp.setDoc_id(empbasic.getDoc_others());
				}
			} catch (Exception e) {
				model.addAttribute("message", e.getMessage());
				logger.error(e.getMessage());
			}
			employeeRepository.save(emp);
			model.addAttribute("message", "Successfully Updated Info For " + emp.getFull_name());
			model.addAttribute("class", "text-success");
		} catch (GenericJDBCException e) {
			e.printStackTrace();
			model.addAttribute("message", e.getMessage());
			logger.error("JDBC: "+e.getMessage());
		} 
		return new ModelAndView("theme/ajaxResponse", model);
	}

	@GetMapping("/employeeList")
	public ModelAndView employeeList(final ModelMap model) {
		model.addAttribute("employees", employeeService.getEmployeeList());
		return new ModelAndView("ems/pages/employeeList", model);
	}

	@GetMapping("/deleteEmployee")
	public ModelAndView deleteEmployee(@RequestParam("empid") Long empid, final ModelMap model) {
		try {
			EMPLOYEE_BASIC emp = employeeRepository.findbyEmpid(empid);
			User user = userService.findUserByEmail(emp.getEmail());
			if (user != null)
				userService.deleteUser(user);
			employeeRepository.delete(emp);
			logger.debug("User deleted:" + emp.getFull_name());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("redirect:/employeeList", model);
	}

	@GetMapping("/viewEmployee")
	public ModelAndView viewEmployee(@RequestParam("empid") Long empid, final ModelMap model) {
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(empid);
		model.addAttribute("userdtl", empdtl);
		return new ModelAndView("ems/ajaxResponse/viewEmployee", model);
	}

	@GetMapping("/editEmployee")
	public ModelAndView editEmployee(@RequestParam("empid") Long empid, final ModelMap model) {
		model.addAttribute("roles", roleRepository.findAll());	
		List<Integer> roles = new ArrayList<Integer>();
		roles.add(4);
		roles.add(6);
		roles.add(7);
		model.addAttribute("chief", employeeRepository.findbyroles(roles));
		EMPLOYEE_BASIC empinfo = employeeService.getEmployeebyID(empid);
		model.addAttribute("empinfo", empinfo);
		User getuser= userService.findUserByEmail(empinfo.getEmail());
		model.addAttribute("user",getuser);
		return new ModelAndView("ems/pages/editEmployee", model);
	}

}
