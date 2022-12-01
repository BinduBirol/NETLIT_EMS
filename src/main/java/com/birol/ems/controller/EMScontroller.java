package com.birol.ems.controller;

//import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.birol.ems.dao.ComplaintsRepo;
import com.birol.ems.dao.LoggedinUserRepo;
import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.dto.LoggedinUserDTO;
import com.birol.ems.dto.SocialMediaLinksDTO;
import com.birol.ems.project.dao.ProjectDao;
import com.birol.ems.project.dto.Project;
import com.birol.ems.project.dto.Project_Workers;
import com.birol.ems.repo.EmployeeRepository;
import com.birol.ems.service.EMSservice;
import com.birol.ems.service.EmployeeService;
import com.birol.persistence.dao.RoleRepository;
import com.birol.persistence.model.User;
import com.birol.security.ActiveUserStore;
import com.birol.service.IUserService;
import com.birol.web.util.GenericResponse;

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
	ComplaintsRepo complaintsRepo;
	@Autowired
	com.birol.ems.dao.CommentRepo commentRepo;
	@Autowired
	LoggedinUserRepo loggedinUserRepo;
	@Autowired
	EMSservice emsService;
	@Autowired
	private ProjectDao projectDao;
	
	private static final Logger logger = LoggerFactory.getLogger(EMScontroller.class);

	@GetMapping("/dashboard")
	public ModelAndView dashboard(final ModelMap model, Authentication auth) {		
		User user = (User) auth.getPrincipal();
		String pattern = "yyyyMMdd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		
		EMPLOYEE_BASIC user_emp = employeeService.getEmployeebyID(user.getId());
		try {
			user_emp.getSocialMediaLinks().hashCode();
		} catch (NullPointerException e) {
			SocialMediaLinksDTO sml= new SocialMediaLinksDTO(user.getId());
			user_emp.setSocialMediaLinks(sml);
		}
	
		model.addAttribute("latest",employeeService.getLatestEmployeeList());
		model.addAttribute("user",user_emp);
		
		
		return new ModelAndView("homepage", model);
	}
	
	@GetMapping("/getLoggedInUsers")
	public ModelAndView getLoggedInUsers(final ModelMap model) {
		ArrayList<LoggedinUserDTO> luserlist= (ArrayList<LoggedinUserDTO>) activeUserStore.getLoggedusers();
		
		for(LoggedinUserDTO u: luserlist) {
			EMPLOYEE_BASIC emp= new EMPLOYEE_BASIC();
			emp= employeeRepository.findbyWorkMail(u.getEmail());
			u.setId(emp.getEmpid());
			u.setFullname(emp.getFull_name());
			if (emp.getEmp_image() != null) {
				String imageencode = Base64.getEncoder().encodeToString(emp.getEmp_image());				
				u.setImage_encoded(imageencode);
			}			
		}
	
		Collections.sort(luserlist, Collections.reverseOrder());
		model.addAttribute("luserlist", luserlist);
		return new ModelAndView("ems/ajaxResponse/viewLoggedinusers", model);
	}

	

	@GetMapping("/changePassword")
	public ModelAndView changePassword(final ModelMap model) {
		return new ModelAndView("ems/pages/changePassword", model);
	}

	@GetMapping("/settings")
	public ModelAndView settings(final ModelMap model) {
		return new ModelAndView("ems/pages/setting", model);
	}

	

	@GetMapping("/test")
	public ModelAndView test(final ModelMap model) throws MessagingException {

		// logger.info("Spring Mail - Sending Email with Inline Attachment Example");
		model.addAttribute("message", "Success");
		return new ModelAndView("theme/ajaxResponse", model);
	}

	@GetMapping("/download/user")
	public void downloadUsersFile(@RequestParam String fileId, @RequestParam Long userid, HttpServletResponse resp,
			Principal principal) throws IOException {

		EMPLOYEE_BASIC emp = new EMPLOYEE_BASIC();
		emp = employeeRepository.findbyEmpid(userid);
		byte[] byteArray = null;
		String ext = ".pdf";
		if (fileId.equalsIgnoreCase("cv")) {
			byteArray = emp.getDoc_cv();
			// ext=Magic.getMagicMatch(bdata).getExtension();
		} else if (fileId.equalsIgnoreCase("crt")) {
			byteArray = emp.getDoc_certificate();
		} else if (fileId.equalsIgnoreCase("others")) {
			byteArray = emp.getDoc_others();
		} else if (fileId.equalsIgnoreCase("id")) {
			byteArray = emp.getDoc_id();
			InputStream is = new ByteArrayInputStream(byteArray);
			String mimeType = URLConnection.guessContentTypeFromStream(is);
			if (mimeType != null) {
				ext = "." + mimeType.split("/")[1];
			}
			System.out.println(mimeType);
		}
		resp.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM.getType());
		resp.setHeader("Content-Disposition", "attachment; filename=" + emp.getEmail() + fileId + ext);
		resp.setContentLength(byteArray.length);
		OutputStream os = resp.getOutputStream();
		try {
			os.write(byteArray, 0, byteArray.length);
		} finally {
			os.close();
		}
	}

	@GetMapping("/user/generate/2faqr")
	@ResponseBody
    public GenericResponse generate2faQR(Authentication auth) throws UnsupportedEncodingException {     
        User user = (User) auth.getPrincipal();
        return new GenericResponse(userService.generateQRUrl(user));
    }
	
	
	@GetMapping("/fetchusersbyrole")
	@ResponseBody
	public ArrayList<EMPLOYEE_BASIC> getusersbyrole(@RequestParam int roleid){
		ArrayList<EMPLOYEE_BASIC> users = employeeRepository.findbyrole(String.valueOf(roleid));
		return users;
	}
	
	@GetMapping("/fetchallusers")
	@ResponseBody
	public ArrayList<EMPLOYEE_BASIC> fetchallusers(@RequestParam(value="roleid") int roleid,
			@RequestParam(value="searchtext") String searchtext){
		ArrayList<EMPLOYEE_BASIC> users = employeeService.getEmployeeListForSearch(roleid,searchtext);
		return users;
	}
}
