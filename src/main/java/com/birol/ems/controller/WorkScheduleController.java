package com.birol.ems.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.birol.ems.dao.AvailablityRepo;
import com.birol.ems.dao.EmpWSHrepo;
import com.birol.ems.dto.Availability;
import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.dto.Employee_work_schedule;
import com.birol.ems.dto.LoggedinUserDTO;
import com.birol.ems.repo.EmployeeRepository;
import com.birol.ems.service.EmployeeService;
import com.birol.ems.service.WSHservice;
import com.birol.persistence.model.User;

@Controller
public class WorkScheduleController {
	@Autowired
	EmployeeService employeeService;
	@Autowired
	EmployeeRepository employeeRepository;
	@Autowired
	WSHservice wSHservice;
	@Autowired
	EmpWSHrepo empWSHrepo;
	@Autowired
	AvailablityRepo avrepo;
	
	@GetMapping("/work_schedule_home")
	public ModelAndView work_schedule_home(final ModelMap model) {		
		model.addAttribute("employees", employeeService.getEmployeeList());
		return new ModelAndView("ems/pages/work_schedule_home", model);
	}
	
	@GetMapping("/work_schedule_new")
	public ModelAndView work_schedule_new(final ModelMap model) {		
		model.addAttribute("employees", employeeService.getEmployeeList());
		return new ModelAndView("ems/pages/work_schedule_new", model);
	}

	@GetMapping("/workschedule")
	public ModelAndView workschedule(@RequestParam("empid") Long empid, final ModelMap model) {
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(empid);
		model.addAttribute("userdtl", empdtl);		
		return new ModelAndView("ems/ajaxResponse/viewworkschedule", model);
	}
	
	@GetMapping("/setavailablity")
	public ModelAndView setavailablity(@RequestParam("empid") Long empid, final ModelMap model) {
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(empid);
		model.addAttribute("userdtl", empdtl);		
		return new ModelAndView("ems/ajaxResponse/setavailablitymodal", model);
	}
	
	@GetMapping("/workschedulehistory")
	public ModelAndView workschedulehistory(@RequestParam("empid") Long empid, final ModelMap model) {
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(empid);
		ArrayList<Employee_work_schedule> empwsh= new ArrayList<Employee_work_schedule>();
		empwsh=(ArrayList<Employee_work_schedule>) empWSHrepo.findByUserid(empid);		
		model.addAttribute("emp", empdtl);
		model.addAttribute("wsh", empwsh);
		return new ModelAndView("ems/ajaxResponse/viewWorkShHistory", model);
	}
	
	@RequestMapping(value = "/saveWSH", method = RequestMethod.POST)
	public ModelAndView addEmployeeDo(@ModelAttribute Employee_work_schedule wsh, ModelMap model, Authentication auth,
			final HttpServletRequest request)  {
		try {
			User creator = (User) auth.getPrincipal();
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        LocalDate fromLocalDate = LocalDate.parse(wsh.getFrom_date(), formatter);
	        LocalDate toLocalDate = LocalDate.parse(wsh.getTo_date(), formatter);
			List<LocalDate> dates= wSHservice.getDatesBetween(fromLocalDate,toLocalDate);
			
			LocalTime l1 = null, l2 = null;
			if(wsh.getStatus()==1) {
				l1 = LocalTime.parse(wsh.getWork_start());
				l2 = LocalTime.parse(wsh.getWork_end());
			}			
	
			for(LocalDate d:dates) {
				wsh.setDate(d);
				wsh.setWork_sh_id(wSHservice.generateWShID(wsh));
				wsh.setAssigned_by_id(creator.getId());
				wsh.setAssigned_by_full_name(creator.getFirstName()+" "+creator.getLastName());
				try{wsh.setWork_minute((Duration.between(l1, l2).toMinutes())-wsh.getLunch_hour());}catch (Exception e) {wsh.setWork_minute(0);}
				Date xdate = new SimpleDateFormat("yyyy-M-d").parse(wsh.getDate().toString());
				wsh.setDay(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(xdate));
				wsh.setWeek(d.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
				empWSHrepo.save(wsh);
			}
			model.addAttribute("message", "Successfully added work schedule for "+wsh.getFull_name());
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
		}
		
		return new ModelAndView("theme/ajaxResponse.html", model);
	}
	
	@RequestMapping(value = "/saveWAV", method = RequestMethod.POST)
	public ModelAndView saveWAV(@ModelAttribute Availability av, ModelMap model, Authentication auth,
			final HttpServletRequest request)  {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        LocalDate fromLocalDate = LocalDate.parse(av.getFrom_date(), formatter);
	        LocalDate toLocalDate = LocalDate.parse(av.getTo_date(), formatter);
			List<LocalDate> dates= wSHservice.getDatesBetween(fromLocalDate,toLocalDate);
			
			LocalTime l1 = null, l2 = null;
			if(av.getStatus()==1) {
				l1 = LocalTime.parse(av.getWork_start());
				l2 = LocalTime.parse(av.getWork_end());
			}
			
			for(LocalDate d:dates) {
				av.setDate(d);
				av.setAv_id(wSHservice.generateWavID(av));				
				try{av.setWork_minute((Duration.between(l1, l2).toMinutes())-av.getLunch_hour());}catch (Exception e) {av.setWork_minute(0);}
				Date xdate = new SimpleDateFormat("yyyy-M-d").parse(av.getDate().toString());
				av.setDay(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(xdate));
				av.setWeek(d.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
				avrepo.save(av);
			}
			model.addAttribute("message", "Successfully added availablity for "+av.getFull_name());
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
		}
		
		return new ModelAndView("theme/ajaxResponse.html", model);
	}
	
	@GetMapping("/checkwshdate")
	public ModelAndView checkwshdate(
			@RequestParam("empid") Long empid,
			@RequestParam("fromdate") Long fromdate,
			@RequestParam("todate") Long todate,
			final ModelMap model) {
		
		return new ModelAndView("theme/ajaxResponse.html", model);
	}
	
	@GetMapping("/timeReport")
	public ModelAndView timeReport(@RequestParam(required = false) String from_date,
			@RequestParam(required = false) String to_date,
			@RequestParam(required = false) String c,
			@RequestParam(required = false) String empid,
			Authentication auth,final ModelMap model) {
		User user = (User) auth.getPrincipal();
		long userid = 0;	
		if(empid==null) {userid=user.getId();}else{userid=Long.parseLong(empid);}	
		
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(userid);
		model.addAttribute("emp", empdtl);
				
		ArrayList<Employee_work_schedule> ewsh = null;
		ewsh= empWSHrepo.getAllBetweenDates(userid,from_date,to_date);
		long total_wh= 0;
		for(Employee_work_schedule w: ewsh) {
			total_wh+= w.getWork_minute();
		}
		model.addAttribute("wsh", ewsh);
		model.addAttribute("total_days", ewsh.size());
		model.addAttribute("total_work", wSHservice.mintsTOHmConvert(total_wh));
		model.addAttribute("emps", employeeService.getEmployeeList());
		return new ModelAndView("ems/pages/timeReport", model);
	}
	
	@GetMapping("/getAvailablity")
	public ModelAndView getAvailablity(final ModelMap model,Authentication auth) {
		User user = (User) auth.getPrincipal();
		String todate;
		ArrayList<Availability> avlist= avrepo.getAvailablityByuseridandDategraterthanToday(user.getId());
		
		model.addAttribute("avlist", avlist);
		return new ModelAndView("ems/ajaxResponse/getAvailablity", model);
	}
	
	
}
