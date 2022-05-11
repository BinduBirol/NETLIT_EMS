package com.birol.ems.controller;

import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.FileAlreadyExistsException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.repo.EmployeeRepository;
import com.birol.ems.service.EmployeeService;
import com.birol.persistence.dao.RoleRepository;
import com.birol.persistence.model.Role;
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
	@Autowired
    private JavaMailSender mailSender;
	@Autowired
    private Environment env;
	
	private static final Logger logger = LoggerFactory.getLogger(EMScontroller.class);
	  
    
	@GetMapping("/dashboard")
	 public ModelAndView dashboard(final ModelMap model, Authentication auth) {		
		model.addAttribute("loggedInUsers", activeUserStore.getUsers());	
		List<String> getUsersFromSessionRegistry= userService.getUsersFromSessionRegistry();
        model.addAttribute("getUsersFromSessionRegistry", getUsersFromSessionRegistry);
        List<User> getAlluser= userService.findAllUser();
        model.addAttribute("getAlluser", getAlluser);
        
		User user = (User)auth.getPrincipal();		
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
	
	@RequestMapping(value="/addEmployeeDo",method=RequestMethod.POST)	
	 public ModelAndView addEmployeeDo(@ModelAttribute EMPLOYEE_BASIC emp,ModelMap model, Authentication auth,final HttpServletRequest request) {
		try {			
			//check for mail existence			
			EMPLOYEE_BASIC checkEmp=employeeRepository.findbyWorkMail(emp.getEmail());
			if(checkEmp!=null) {
				model.addAttribute("message","Already exists an employee with email: "+emp.getEmail()+"\nPlease try another work email.");				
				return new ModelAndView("theme/ajaxResponse", model);			
			}
			
			if(emp.getEmp_image_m().getSize()>0) emp.setEmp_image(emp.getEmp_image_m().getBytes());
			if(emp.getDoc_m_cv().getSize()>0)emp.setDoc_cv(emp.getDoc_m_cv().getBytes());
			if(emp.getDoc_m_crt().getSize()>0)emp.setDoc_certificate(emp.getDoc_m_crt().getBytes());
			if(emp.getDoc_m_id().getSize()>0)emp.setDoc_id(emp.getDoc_m_id().getBytes());
			
			User empCreator = (User)auth.getPrincipal();
			emp.setAdded_by(empCreator.getEmail());
			employeeRepository.save(emp);			
			//send mail
			SimpleMailMessage email= new SimpleMailMessage();
			email.setTo(emp.getEmail());
			email.setSubject("Information added");
			final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
			String emailText="Dear "+emp.getFull_name()
					+",\nYour employment information succesfully added to NETLIT EMS system"
					+ "\nGo to the link to create your account:"
					+ "\n"+appUrl+"/registration.html";
			email.setText(emailText);
			email.setFrom(env.getProperty("support.email"));
			mailSender.send(email);
			
			model.addAttribute("message","Successfully Added Info For "+emp.getEmail());
			model.addAttribute("class","alert alert-success");
		}catch (FileAlreadyExistsException fe) {	
			fe.printStackTrace();
			model.addAttribute("message",fe.getMessage());
			logger.error(fe.getMessage());
		}catch (Exception e) {		
			e.printStackTrace();
			model.addAttribute("message",e.getMessage());
			logger.error(e.getMessage());
		}
		return new ModelAndView("theme/ajaxResponse", model);
	}
	
	@RequestMapping(value="/editEmployeeDo",method=RequestMethod.POST)	
	 public ModelAndView editEmployeeDo(@ModelAttribute EMPLOYEE_BASIC emp,ModelMap model, Authentication auth,final HttpServletRequest request) {
		try {
			EMPLOYEE_BASIC empbasic= new EMPLOYEE_BASIC();
			empbasic= employeeRepository.findbyEmpid(emp.getEmpid());
			User user= userService.findUserByEmail(empbasic.getEmail());
			if(user!=null) {
				Role role = roleRepository.getById((long)emp.getRoleid());
				user.setRoles(Arrays.asList(role));
				userService.updateUserInfo(user);
			}			
			
			try {
				if (emp.getEmp_image_m().getSize() > 0) {
					emp.setEmp_image(emp.getEmp_image_m().getBytes());
				}else {
					emp.setEmp_image(empbasic.getEmp_image());
				}
			} catch (Exception e) {
				model.addAttribute("message",e.getMessage());
				logger.error(e.getMessage());
			}			
			//documents
			try {
				if (emp.getDoc_m_cv().getSize() > 0) {
					emp.setDoc_cv(emp.getDoc_m_cv().getBytes());
				}else {
					emp.setDoc_cv(empbasic.getDoc_cv());
				}
			} catch (Exception e) {
				model.addAttribute("message", e.getMessage());
				logger.error(e.getMessage());
			}
			try {
				if (emp.getDoc_m_crt().getSize() > 0) {
					emp.setDoc_certificate(emp.getDoc_m_crt().getBytes());
				}else {
					emp.setDoc_certificate(empbasic.getDoc_certificate());
				}
			} catch (Exception e) {
				model.addAttribute("message", e.getMessage());
				logger.error(e.getMessage());
			} 
			try {
				if (emp.getDoc_m_id().getSize() == 0) {
					emp.setDoc_id(emp.getDoc_m_id().getBytes());
				}else {
					emp.setDoc_id(empbasic.getDoc_id());
				}
			} catch (Exception e) {
				model.addAttribute("message", e.getMessage());
				logger.error(e.getMessage());
			}
			try {
				if (emp.getDoc_m_others().getSize() == 0) {
					emp.setDoc_id(emp.getDoc_m_others().getBytes());
				}else {
					emp.setDoc_id(empbasic.getDoc_others());
				}
			} catch (Exception e) {
				model.addAttribute("message", e.getMessage());
				logger.error(e.getMessage());
			}	
			employeeRepository.save(emp);			
			model.addAttribute("message","Successfully Updated Info For "+emp.getEmail());
			model.addAttribute("class","alert alert-success");
		}catch (Exception e) {		
			e.printStackTrace();
			model.addAttribute("message",e.getMessage());
			logger.error(e.getMessage());
		}
		return new ModelAndView("theme/ajaxResponse", model);
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
		EMPLOYEE_BASIC empdtl= employeeRepository.findbyEmpid(user.getId());
		if(empdtl.getEmp_image()!=null) {
			String imageencode = Base64.getEncoder().encodeToString(empdtl.getEmp_image());
			empdtl.setEmp_image_encoded(imageencode);			    	
		}
		try {
			Period p = Period.between(LocalDate.now(),LocalDate.parse(empdtl.getContract_end().replace("/", "-")));				
			String format_p=p.toString().replace("P", "").replace("Y", "Years ").replace("M", "Months ").replace("D", "Days");				
			empdtl.setContact_status_str("align-middle");
			if(format_p.startsWith("-"))empdtl.setContact_status_str("text-danger");
			empdtl.setContact_remaining_period(format_p);
		} catch (Exception e2) {
			empdtl.setContact_remaining_period("Not spacified");
		}
		model.addAttribute("user",user);
		model.addAttribute("userdtl",empdtl);
		return new ModelAndView("ems/pages/profile", model);		
	}
	
	@RequestMapping(value = "/savePersonalInfo", method=RequestMethod.POST)
	 public ModelAndView savePersonalInfo(@ModelAttribute EMPLOYEE_BASIC personal, final ModelMap model) {		
		EMPLOYEE_BASIC empbasic= new EMPLOYEE_BASIC();
		empbasic= employeeRepository.findbyEmpid(personal.getEmpid());		
		try {
			if (personal.getEmp_image_m().getSize() > 0) {
				empbasic.setEmp_image(personal.getEmp_image_m().getBytes());
			}
		} catch (Exception e) {
			model.addAttribute("message",e.getMessage());
			logger.error(e.getMessage());			
		}
		empbasic.setFirst_name(personal.getFirst_name());
		empbasic.setLast_name(personal.getLast_name());
		empbasic.setPrivate_email(personal.getPrivate_email());
		empbasic.setClosest_relative_full(personal.getClosest_relative_full());
		empbasic.setAddress_full(personal.getAddress_full());
		empbasic.setSocial_security_number(personal.getSocial_security_number());
		empbasic.setPhone_eve(personal.getPhone_eve());
		empbasic.setSex(personal.getSex());
		//set bank info
		empbasic.setBank_name(personal.getBank_name());
		empbasic.setAccount_number(personal.getAccount_number());
		empbasic.setClearing_number(personal.getClearing_number());
		empbasic.setTable_tax(personal.getTable_tax());
		//documents
		try {
			if (personal.getDoc_m_cv().getSize() > 0) {
				empbasic.setDoc_cv(personal.getDoc_m_cv().getBytes());
			}
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
			logger.error(e.getMessage());
		}
		try {
			if (personal.getDoc_m_crt().getSize() > 0) {
				empbasic.setDoc_certificate(personal.getDoc_m_crt().getBytes());
			}
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
			logger.error(e.getMessage());
		} 
		try {
			if (personal.getDoc_m_id().getSize() > 0) {
				empbasic.setDoc_id(personal.getDoc_m_id().getBytes());
			}
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
			logger.error(e.getMessage());
		}		
		employeeRepository.save(empbasic);
		model.addAttribute("message","Succesfully Updated Info For "+empbasic.getFirst_name());
		model.addAttribute("class","alert alert-success");
		return new ModelAndView("theme/ajaxResponse", model);		
	}
	
	@GetMapping("/settings")
	 public ModelAndView settings(final ModelMap model) {		
		return new ModelAndView("ems/pages/setting", model);		
	}
	
	
	
	@GetMapping("/editEmployee")
	public ModelAndView editEmployee(@RequestParam("empid") Long empid, final ModelMap model){
		model.addAttribute("roles",roleRepository.findAll());
		EMPLOYEE_BASIC empinfo= employeeRepository.findbyEmpid(empid);
		if(empinfo.getEmp_image()!=null) {
			String imageencode = Base64.getEncoder().encodeToString(empinfo.getEmp_image());
			empinfo.setEmp_image_encoded(imageencode);			    	
		}
		model.addAttribute("empinfo",empinfo);
		return new ModelAndView("ems/pages/editEmployee", model);	
	}
	
	@GetMapping("/viewEmployee")
	public ModelAndView viewEmployee(@RequestParam("empid") Long empid, final ModelMap model){
		EMPLOYEE_BASIC empdtl= employeeRepository.findbyEmpid(empid);
		if(empdtl.getEmp_image()!=null) {
			String imageencode = Base64.getEncoder().encodeToString(empdtl.getEmp_image());
			empdtl.setEmp_image_encoded(imageencode);			    	
		}
		try {
			Period p = Period.between(LocalDate.now(),LocalDate.parse(empdtl.getContract_end().replace("/", "-")));				
			String format_p=p.toString().replace("P", "").replace("Y", "Years ").replace("M", "Months ").replace("D", "Days");				
			empdtl.setContact_status_str("align-middle");
			if(format_p.startsWith("-"))empdtl.setContact_status_str("text-danger");
			empdtl.setContact_remaining_period(format_p);
		} catch (Exception e2) {
			empdtl.setContact_remaining_period("Not spacified");
		}
		//model.addAttribute("user",user);
		model.addAttribute("userdtl",empdtl);
		//return new ModelAndView("ems/pages/profile", model);	
		return new ModelAndView("ems/ajaxResponse/viewEmployee", model);
	}
	
	@GetMapping("/deleteEmployee")
	public ModelAndView deleteEmployee(@RequestParam("empid") Long empid, final ModelMap model){		
		try {
			EMPLOYEE_BASIC emp= employeeRepository.findbyEmpid(empid);
			User user= userService.findUserByEmail(emp.getEmail());			
			if(user!=null)userService.deleteUser(user);			
			employeeRepository.delete(emp);
			logger.debug("User deleted:"+ emp.getFull_name());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("redirect:/employeeList", model);
	}
	
	@GetMapping("/download/user")
    public void downloadUsersFile(@RequestParam String fileId,@RequestParam Long userid, HttpServletResponse resp,Principal principal) throws IOException {
  	
    	EMPLOYEE_BASIC emp= new EMPLOYEE_BASIC();		
		emp=employeeRepository.findbyEmpid(userid);
    	byte[] byteArray = null;
    	String ext=".pdf";
    	if(fileId.equalsIgnoreCase("cv")) {
    		byteArray = emp.getDoc_cv();
    		//ext=Magic.getMagicMatch(bdata).getExtension();
        }else if(fileId.equalsIgnoreCase("crt")) {
    		byteArray = emp.getDoc_certificate();
        }else if(fileId.equalsIgnoreCase("others")) {
    		byteArray = emp.getDoc_others();
        }else if(fileId.equalsIgnoreCase("id")) {        	
    		byteArray = emp.getDoc_id();
    		InputStream is = new ByteArrayInputStream(byteArray);
            String mimeType = URLConnection.guessContentTypeFromStream(is);  
            if(mimeType!=null) {
            	ext="."+mimeType.split("/")[1];
            }
            System.out.println(mimeType);
        }
        resp.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM.getType()); 
        resp.setHeader("Content-Disposition", "attachment; filename=" +emp.getEmail()+fileId+ext);
        resp.setContentLength(byteArray.length);
        OutputStream os = resp.getOutputStream();        
        try {
            os.write(byteArray, 0, byteArray.length);
        } finally {
            os.close();
        }
    }
	
}
