package com.birol.ems.contract;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.security.Principal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.thymeleaf.util.StringUtils;

import com.birol.ems.contract.email.EmailTemplateRepo;
import com.birol.ems.contract.email.Email_msg_DTO;
import com.birol.ems.contract.signer.SignerRepo;
import com.birol.ems.contract.signer.Signer_DTO;
import com.birol.ems.controller.EMScontroller;
import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.repo.EmployeeRepository;
import com.birol.persistence.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mysql.cj.jdbc.exceptions.PacketTooBigException;
import com.birol.ems.service.EmailService;

@Controller
public class ContractController {
	
	@Autowired
	EmployeeRepository employeeRepository;
	@Autowired
	EmailTemplateRepo emailTemplateRepo;
	@Autowired
	NewDocRepo newDocRepo;
	@Autowired
	SignerRepo signerRepo;
	@Autowired
	EmailService emailService;
	
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
	
	@GetMapping("/contractMonitoring")
	public ModelAndView contractMonitoring(Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();
		model.addAttribute("myid", user.getId());
		return new ModelAndView("ems/pages/contract/contractMonitoring", model);
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
	
	
	@ResponseBody
	@PostMapping("/saveAndSendContract")
	public String saveAndSendContract(Authentication auth,
			@ModelAttribute NewDocumentForSign_DTO formData) throws SQLException {
		String msg="";
		User user = (User) auth.getPrincipal();
		NewDocumentForSign_DTO savedObj= new NewDocumentForSign_DTO();
		try {			
			if(formData.getDocument_file_m().getSize()>0)formData.setDocument_file(formData.getDocument_file_m().getBytes());
			
			formData.setValid_till(LocalDate.parse(formData.getValid_till_str()));
			if(formData.getInvite_as().equals("me")) {formData.setCreator_id(user.getId());}else {formData.setCreator_id(0);}
			formData.setCreator_name(user.getFirstName()+" "+user.getLastName());
			formData.setStatus("pending");
			savedObj=newDocRepo.save(formData);			
			
			Gson gson = new Gson(); 
			List<Signer_DTO> signerList = gson.fromJson(formData.getSigners_str(), new TypeToken<List<Signer_DTO>>(){}.getType());
			for(Signer_DTO s: signerList) {
				s.setContractid(savedObj.getId());
				s.setSigning_password(getSigningPassword());
				boolean mailsent = false;
				try {
					mailsent=emailService.sendContractSigningMail(s, formData.getEmail_message());
				}catch (Exception e) {
					e.printStackTrace();
				}				
				s.setIsmailsent(mailsent);
				signerRepo.save(s);
			}
			msg="New contract invitation sent succesfully";					
		}catch (Exception e) {			
			e.printStackTrace();			
			msg=e.getMessage();		    
		}
		return msg;
	}
	
	
	
	@PostMapping("/getContractTable")
	public ModelAndView getContractTable(@RequestPayload String from_date,
										@RequestPayload String to_date,												
										Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();
		ArrayList<NewDocumentForSign_DTO> contractlistall= new ArrayList<NewDocumentForSign_DTO>();
		ArrayList<NewDocumentForSign_DTO> contractlist= new ArrayList<NewDocumentForSign_DTO>();
		
		contractlistall= (ArrayList<NewDocumentForSign_DTO>) newDocRepo.findAllByCreator_idOrderByCreatedDesc(0);
		contractlist= (ArrayList<NewDocumentForSign_DTO>) newDocRepo.findAllByCreator_idOrderByCreatedDesc(user.getId());		
		contractlist.addAll(contractlistall);
		for(NewDocumentForSign_DTO s: contractlist) {
			int c=0;
			for(Signer_DTO sd: s.getSigners()) {				
				if(sd.issigned)c++;					
			}
			s.setSigned_count(c);
			try{s.setSigner_percentage((100/s.getSigners().size())*s.getSigned_count());}catch (Exception e) {
				s.setSigner_percentage(100);
			}
		}
		Collections.sort(contractlist, Collections.reverseOrder());
		model.addAttribute("list", contractlist);	
		return new ModelAndView("ems/ajaxResponse/contract/getContractTable", model);		
	}
	
	
	@GetMapping("/download/contract")
	public void downloadcontractFile(@RequestParam Long contractid, HttpServletResponse resp,
			Principal principal) throws IOException {

		NewDocumentForSign_DTO contract= newDocRepo.findById(contractid).get();
		byte[] byteArray = null;
		String ext = ".pdf";
		byteArray = contract.getDocument_file();
		resp.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM.getType());
		resp.setHeader("Content-Disposition", "attachment; filename=" + contract.getDoc_name() + ext);
		resp.setContentLength(byteArray.length);
		OutputStream os = resp.getOutputStream();
		try {
			os.write(byteArray, 0, byteArray.length);
		} finally {
			os.close();
		}
	}
	
	@GetMapping("/contractview")
	public ModelAndView contractview(@RequestParam("contractid") Long contractid, final ModelMap model) {		
		NewDocumentForSign_DTO contract = new NewDocumentForSign_DTO();
		contract=newDocRepo.findById(contractid).get();
		int c=0;
		for(Signer_DTO sd: contract.getSigners()) {				
			if(sd.issigned)c++;					
		}
		contract.setSigned_count(c);
		try{contract.setSigner_percentage((100*contract.getSigned_count())/contract.getSigners().size());}catch (Exception e) {
			contract.setSigner_percentage(100);
		}		
		model.addAttribute("contract", contract);
		return new ModelAndView("ems/pages/contract/contractview", model);
	}
	
	protected String getSigningPassword() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
}
