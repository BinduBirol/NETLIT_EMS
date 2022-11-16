package com.birol.ems.timereport.re;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;

import com.birol.ems.dao.EmpTimeReportRepo;
import com.birol.ems.dao.EmpWSHrepo;
import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.dto.EmpTimeReportDTO;
import com.birol.ems.repo.EmployeeRepository;
import com.birol.ems.service.EmployeeService;
import com.birol.ems.service.WSHservice;
import com.birol.ems.timereport.dto.Timereport_Overtime_emp;
import com.birol.ems.timereport.repo.AvailabilityRepo;
import com.birol.ems.timereport.repo.EmpOvertimeRepo;
import com.birol.ems.timereport.repo.OvertimeRepo;
import com.birol.persistence.model.User;
import com.birol.service.IUserService;
import com.google.gson.Gson;

@Controller
public class TimeReportControllerNew {
	
	@Autowired
	TimeReportTypesRepo timeReportTypesRepo;
	@Autowired
	Time_Report_Repo time_Report_Repo;
	
	@Autowired
	EmployeeService employeeService;
	@Autowired
	EmployeeRepository employeeRepository;
	@Autowired
	WSHservice wSHservice;
	@Autowired
	EmpWSHrepo empWSHrepo;
	@Autowired
	EmpTimeReportRepo avrepo;	
	@Autowired
	EmpOvertimeRepo obrepo;
	@Autowired
    private OvertimeRepo overtimeRepo;
    @Autowired
    private AvailabilityRepo availabilityRepo;
    @Autowired
	IUserService userService;
    @Autowired
	private JavaMailSender mailSender;
	@Autowired
	private Environment env;
	
	@GetMapping("/approvedWorkTimeHome")
	public ModelAndView approvedWorkTimeHome(Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(user.getId());
		model.addAttribute("emps", employeeRepository.getbyChief(user.getId()));
		model.addAttribute("emp", empdtl);		
		return new ModelAndView("ems/pages/timeReport/approvedWorkTimeHome", model);	
	}
	
	@GetMapping("/pendingTimeReportsHome")
	public ModelAndView pendingTimeReportsHome(Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(user.getId());
		model.addAttribute("emps", employeeRepository.getbyChief(user.getId()));
		model.addAttribute("emp", empdtl);		
		
		return new ModelAndView("ems/pages/pendingTimeReports", model);		
	}
	
	@GetMapping("/reportWorkTimeHome")
	public ModelAndView reportWorkTimeHome(Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(user.getId());
		model.addAttribute("userdtl", empdtl);
		return new ModelAndView("ems/pages/timeReport/reportWorkTimeHome", model);
	}
	
	@GetMapping("/getVacationRequestModal")
	public ModelAndView vacationRequestModal(@RequestParam("empid") Long empid, final ModelMap model) {
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(empid);
		model.addAttribute("avtype", timeReportTypesRepo.findByisWorking(false));
		model.addAttribute("userdtl", empdtl);
		return new ModelAndView("ems/ajaxResponse/timereport/vacationRequestModal", model);
	}
	
	@RequestMapping(value = "/saveVacationRequest", method = RequestMethod.POST)
	public ModelAndView saveVacationRequest(@ModelAttribute Time_Report_DTO av, ModelMap model, Authentication auth,
			final HttpServletRequest request) {
		try {
			User creator = (User) auth.getPrincipal();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate fromLocalDate = LocalDate.parse(av.getFrom_date(), formatter);
			LocalDate toLocalDate = LocalDate.parse(av.getTo_date(), formatter);
			List<LocalDate> dates = wSHservice.getDatesBetween(fromLocalDate, toLocalDate);			
			String msg="";

			for (LocalDate d : dates) {				
				av.setEmpid(creator.getId());
				av.setFull_name(creator.getFirstName()+" "+creator.getLastName());
				av.setDate(d);
				av.setTr_id(wSHservice.generateTimeReportID(av,1));
				av.setWork_interval(0);
				av.setWork_minute(0);
				av.setWork_hour("0 H 0 Min");				
				Date xdate = new SimpleDateFormat("yyyy-M-d").parse(av.getDate().toString());
				av.setDay(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(xdate));
				av.setWeek(d.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
				
				if(d.isBefore(LocalDate.now())) {
					  msg+="Cant set vacation request for past date "+d+". ";
				}else {
					msg+="Vacation requested for date "+d+". ";
					time_Report_Repo.save(av);
				}					
			}
			model.addAttribute("message",msg);
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
		}
		return new ModelAndView("theme/ajaxResponse.html", model);
	}

	@SuppressWarnings("null")
	@GetMapping("/getTimeFormsBydate")
	public ModelAndView getTimeFormsBydate(@RequestParam String from_date,@RequestParam String to_date, Authentication auth, final ModelMap model){
		User user = (User) auth.getPrincipal();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate fromLocalDate = LocalDate.parse(from_date, formatter);
		LocalDate toLocalDate = LocalDate.parse(to_date, formatter);
		List<LocalDate> dates = wSHservice.getDatesBetween(fromLocalDate, toLocalDate);		
		Time_Report_DTO existing= new Time_Report_DTO();
		Time_Report_DTO tr= new Time_Report_DTO();
		TreeMap<LocalDate,List<Time_Report_DTO>> trmap = new TreeMap<>();
		List<Time_Report_DTO> trlist=new LinkedList<Time_Report_DTO>();
		
		for(LocalDate d:dates) {
			trlist=new LinkedList<Time_Report_DTO>();
			for(int i=1; i<=3;i++) {				
				tr= new Time_Report_DTO();
				tr.setEmpid(user.getId());
				tr.setDate(d);
				String trid= new String(wSHservice.generateTimeReportID(tr, i));
				tr.setTr_id(trid);
				existing= new Time_Report_DTO();
				try{
					existing= time_Report_Repo.findById(trid).get();
					trlist.add(existing);				
				}catch (NoSuchElementException e) {
					tr.setWork_minute(0);
					tr.setWork_hour("0 H 00 Min");
					trlist.add(tr);
				}				
			}
			trmap.put(d, trlist);
		}
		
		model.addAttribute("trtypeALL", timeReportTypesRepo.findAll());
		model.addAttribute("trtypeRG", timeReportTypesRepo.findByisOB(false));
		model.addAttribute("trtypeOB", timeReportTypesRepo.findByisOB(true));
		model.addAttribute("trlist", trlist);
		model.addAttribute("trmap", trmap);
		return	new ModelAndView("ems/ajaxResponse/timereport/getTimereportFormsTable",model);	
	}
	
	@ResponseBody
	@RequestMapping(value = "/saveTimeReport", method = RequestMethod.POST)
	public String saveTimeReport(@RequestPayload String str, Authentication auth,
			final HttpServletRequest request) {
		Gson gson = new Gson(); 
		Time_Report_DTO av= gson.fromJson(str, Time_Report_DTO.class);;
		
		String msg="";
		User user = (User) auth.getPrincipal();
		av.setEmpid(user.getId());
		av.setFull_name(user.getFirstName()+" "+user.getLastName());
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate avDate = LocalDate.parse(av.getFrom_date(), formatter);
			Date xdate = new SimpleDateFormat("yyyy-M-d").parse(avDate.toString());
			av.setDate(avDate);
			av.setDay(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(xdate));
			av.setWeek(av.getDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));			
			if(LocalDate.now().compareTo(avDate)<0 && av.getWork_minute()>0) {
				msg="Cant report for advance date: "+avDate;
			}else {
				time_Report_Repo.save(av);
				msg="Successfully reported for date: " + av.getFrom_date();
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg= e.getMessage();
		}

		return msg;
	}
}
