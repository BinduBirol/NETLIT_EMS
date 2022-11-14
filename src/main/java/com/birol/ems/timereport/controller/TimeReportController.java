package com.birol.ems.timereport.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;

import com.birol.ems.dao.EmpTimeReportRepo;
import com.birol.ems.dao.EmpWSHrepo;
import com.birol.ems.dto.EmpTimeReportDTO;
import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.dto.Time_report_approved;
import com.birol.ems.dto.LoggedinUserDTO;
import com.birol.ems.dto.Mail;
import com.birol.ems.repo.EmployeeRepository;
import com.birol.ems.service.EmailService;
import com.birol.ems.service.EmployeeService;
import com.birol.ems.service.WSHservice;
import com.birol.ems.timereport.dto.Timereport_Overtime_emp;
import com.birol.ems.timereport.repo.AvailabilityRepo;
import com.birol.ems.timereport.repo.EmpOvertimeRepo;
import com.birol.ems.timereport.repo.OvertimeRepo;
import com.birol.persistence.model.User;
import com.birol.service.IUserService;
import com.birol.service.UserService;
import com.google.gson.Gson;

@Controller
public class TimeReportController {
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

	@GetMapping("/work_schedule_home")
	public ModelAndView work_schedule_home(final ModelMap model) {
		model.addAttribute("employees", employeeService.getEmployeeList());
		return new ModelAndView("ems/pages/work_schedule_home", model);
	}

	@GetMapping("/empsTimereport")
	public ModelAndView empsTimereport(@RequestParam(required = false) String from_date,
			@RequestParam(required = false) String to_date, @RequestParam(required = false) String c,
			@RequestParam(required = false) String empid, Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(user.getId());
		model.addAttribute("emps", employeeRepository.getbyChief(user.getId()));
		model.addAttribute("emp", empdtl);
		
	
		ArrayList<EmpTimeReportDTO> availist = new ArrayList<EmpTimeReportDTO>();	

		if (empid == null & from_date == null) {
			ArrayList<EMPLOYEE_BASIC> myemps= employeeRepository.getbyChief(user.getId());
			Set<EmpTimeReportDTO> myeavaiset = new LinkedHashSet<EmpTimeReportDTO>();	
			for(EMPLOYEE_BASIC x: myemps) {
				myeavaiset.addAll(avrepo.getAvailablityByuseridDatelessthanToday((x.getEmpid())));	
			}
			availist = new ArrayList<>(myeavaiset);
		}else if (empid.startsWith("E")) {
			availist = avrepo.getUserBetweenDates(Long.parseLong(empid.split("-")[1]), from_date, to_date);			
		}else if (empid.equals("all")) {
			availist = avrepo.getAllusersBetweenDates(from_date, to_date);			
		}else if (empid.equals("me")) {
			ArrayList<EMPLOYEE_BASIC> myemps= employeeRepository.getbyChief(user.getId());
			Set<EmpTimeReportDTO> myeavaiset = new LinkedHashSet<EmpTimeReportDTO>();	
			for(EMPLOYEE_BASIC x: myemps) {
				myeavaiset.addAll( avrepo.getUserBetweenDates((x.getEmpid()), from_date, to_date));
			}
			availist = new ArrayList<>(myeavaiset);
		}
		
		ArrayList<EmpTimeReportDTO> finalavailist = new ArrayList<EmpTimeReportDTO>();	
		for(EmpTimeReportDTO x: availist) {
			boolean add= false;
			if(!x.isIsapproved() && !x.isIsrejected()) add= true;
			
			for(Timereport_Overtime_emp o: x.getOvertime()) {
				if(!o.isIsapproved() && !o.isIsrejected())add=true;
			}
			if(add)finalavailist.add(x);
		}
		
		model.addAttribute("avtype", availabilityRepo.findAll());
		model.addAttribute("obtype", overtimeRepo.findAll());
		model.addAttribute("wsh", finalavailist);

		return new ModelAndView("ems/pages/empsTimereport", model);
	}
	
	@GetMapping("/pendingTimeReportsHome")
	public ModelAndView pendingTimeReportsHome(Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(user.getId());
		model.addAttribute("emps", employeeRepository.getbyChief(user.getId()));
		model.addAttribute("emp", empdtl);		
		
		return new ModelAndView("ems/pages/pendingTimeReports", model);		
	}
	
	@PostMapping("/getpendingTimeReports")
	public ModelAndView getpendingTimeReports(	@RequestPayload String from_date,
												@RequestPayload String to_date,
												@RequestPayload String emp_id,
												Authentication auth, final ModelMap model) {
		
		User user = (User) auth.getPrincipal();
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(user.getId());
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate fromLocalDate = LocalDate.parse(from_date, formatter);
		LocalDate toLocalDate = LocalDate.parse(to_date, formatter);
		List<LocalDate> dates = wSHservice.getDatesBetween(fromLocalDate, toLocalDate);	
		ArrayList<EMPLOYEE_BASIC> myemps= employeeRepository.getbyChief(user.getId());
		
		Map<LocalDate, ArrayList<EmpTimeReportDTO>> map= new HashMap<LocalDate, ArrayList<EmpTimeReportDTO>>();
		Map<LocalDate, ArrayList<Timereport_Overtime_emp>> obmap= new HashMap<LocalDate, ArrayList<Timereport_Overtime_emp>>();
		
		
		for(LocalDate d: dates) {
			ArrayList<EmpTimeReportDTO> trlist = new ArrayList<EmpTimeReportDTO>();
			ArrayList<Timereport_Overtime_emp> oblist = new ArrayList<Timereport_Overtime_emp>();
			
			if(emp_id.equals("all")) {
				trlist = new ArrayList<EmpTimeReportDTO>();
				oblist = new ArrayList<Timereport_Overtime_emp>();
				trlist= avrepo.getAllusersOneDate(d);
				oblist=obrepo.getAllusersOneDate(d);
			}else if(emp_id.equals("me")) {				
				for(EMPLOYEE_BASIC x: myemps) {
					EmpTimeReportDTO obj= new EmpTimeReportDTO();
					Timereport_Overtime_emp obobj= new Timereport_Overtime_emp();
					obj=avrepo.findbyOnedateEmpid(x.getEmpid(),d);
					ArrayList<Timereport_Overtime_emp> list=obrepo.findbyOnedateEmpid(x.getEmpid(),d);
					for(Timereport_Overtime_emp o: list)if(o!=null)oblist.add(o);
					if(obj!=null)trlist.add(obj);					
				}
													
			}else {
				trlist = new ArrayList<EmpTimeReportDTO>();
				oblist = new ArrayList<Timereport_Overtime_emp>();
				EmpTimeReportDTO obj= new EmpTimeReportDTO();
				Timereport_Overtime_emp obobj= new Timereport_Overtime_emp();
				obj= avrepo.findbyOnedateEmpid(Long.parseLong(emp_id.split("-")[1]),d);
				ArrayList<Timereport_Overtime_emp> list =obrepo.findbyOnedateEmpid(Long.parseLong(emp_id.split("-")[1]),d);
				for(Timereport_Overtime_emp o: list)if(o!=null)oblist.add(o);
				if(obj!=null)trlist.add(obj);
			}		
			
			//if(trlist.size()>0)map.put(d, trlist);
			map.put(d, trlist);
			//if(oblist.size()>0)obmap.put(d, oblist);
			obmap.put(d, oblist);
		}
		
		model.addAttribute("dates", dates);
		model.addAttribute("avtype", availabilityRepo.findAll());
		model.addAttribute("obtype", overtimeRepo.findAll());
		model.addAttribute("map", map);
		model.addAttribute("obmap", obmap);
		
		return new ModelAndView("ems/ajaxResponse/timereport/getPendingTimeReports", model);	
	}

	@PostMapping("/getApprovedTimeReports")
	public ModelAndView getApprovedTimeReports(	@RequestPayload String from_date,
												@RequestPayload String to_date,												
												Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();
		ArrayList<EmpTimeReportDTO> rglist= avrepo.approvedOneUserFdTd(from_date, to_date, user.getId());
		ArrayList<Timereport_Overtime_emp> oblist= obrepo.approvedOneUserFdTd(from_date, to_date, user.getId());
		
		HashSet<Integer> weeks= new HashSet<>();
		for(EmpTimeReportDTO r: rglist) weeks.add(r.getWeek());
		for(Timereport_Overtime_emp o: oblist) weeks.add(o.getWeek());
		
		HashMap<Integer, ArrayList<EmpTimeReportDTO>> rgmap= new HashMap<>();
		HashMap<Integer, ArrayList<Timereport_Overtime_emp>> obmap= new HashMap<>();
		HashMap<Integer, TotalTRDTO> totalmap= new HashMap<>();
		
		int tRwm=0;
		int tOwm=0;
		TotalTRDTO tobj= new TotalTRDTO();
		
		for(int w: weeks) {
			tRwm=0;
			tOwm=0;
			tobj= new TotalTRDTO();
			ArrayList<EmpTimeReportDTO> rglist2 = new ArrayList<>();
			ArrayList<Timereport_Overtime_emp> oblist2 = new ArrayList<>();;
			for(EmpTimeReportDTO r: rglist) {
				if(r.getWeek()==w) {rglist2.add(r);
				tRwm+= r.getWork_minute();}
			}
			for(Timereport_Overtime_emp o: oblist) {
				if(o.getWeek()==w) {oblist2.add(o);
				tOwm+= o.getWork_minute();}
			}
			tobj.setTotal_rg(wSHservice.mintsTOHmConvert(tRwm));
			tobj.setTotal_ob(wSHservice.mintsTOHmConvert(tOwm));
			totalmap.put(w, tobj);
			rgmap.put(w, rglist2);
			obmap.put(w, oblist2);
		}
		
		model.addAttribute("rgmap", rgmap);
		model.addAttribute("obmap", obmap);
		model.addAttribute("totalmap", totalmap);
		return new ModelAndView("ems/ajaxResponse/timereport/getApprovedTimeReports", model);	
	}
	
	@GetMapping("/approvedWorkTimeHome")
	public ModelAndView approvedWorkTimeHome(Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(user.getId());
		model.addAttribute("emps", employeeRepository.getbyChief(user.getId()));
		model.addAttribute("emp", empdtl);		
		return new ModelAndView("ems/pages/timeReport/approvedWorkTimeHome", model);	
	}	
	
	
	@GetMapping("/workschedule")
	public ModelAndView workschedule(@RequestParam("empid") Long empid, final ModelMap model) {
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(empid);
		model.addAttribute("userdtl", empdtl);
		return new ModelAndView("ems/ajaxResponse/viewworkschedule", model);
	}

	@GetMapping("/settimereport")
	public ModelAndView settimereport(@RequestParam("empid") Long empid, final ModelMap model) {
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(empid);
		model.addAttribute("avtype", availabilityRepo.findAll());
		model.addAttribute("obtype", overtimeRepo.findAll());
		model.addAttribute("userdtl", empdtl);
		return new ModelAndView("ems/ajaxResponse/setavailablitymodal", model);
	}


	@RequestMapping(value = "/saveWSH", method = RequestMethod.POST)
	public ModelAndView addEmployeeDo(@ModelAttribute Time_report_approved wsh, ModelMap model, Authentication auth,
			final HttpServletRequest request) {
		try {
			User creator = (User) auth.getPrincipal();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate fromLocalDate = LocalDate.parse(wsh.getFrom_date(), formatter);
			LocalDate toLocalDate = LocalDate.parse(wsh.getTo_date(), formatter);
			List<LocalDate> dates = wSHservice.getDatesBetween(fromLocalDate, toLocalDate);

			LocalTime l1 = null, l2 = null;
			if (wsh.getStatus() == 1) {
				l1 = LocalTime.parse(wsh.getWork_start());
				l2 = LocalTime.parse(wsh.getWork_end());
			}

			for (LocalDate d : dates) {
				wsh.setDate(d);
				wsh.setWork_sh_id(wSHservice.generateWShID(wsh));
				wsh.setAssigned_by_id(creator.getId());
				wsh.setAssigned_by_full_name(creator.getFirstName() + " " + creator.getLastName());
				try {
					wsh.setWork_minute((Duration.between(l1, l2).toMinutes()) - wsh.getLunch_hour());
				} catch (Exception e) {
					wsh.setWork_minute(0);
				}
				Date xdate = new SimpleDateFormat("yyyy-M-d").parse(wsh.getDate().toString());
				wsh.setDay(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(xdate));
				wsh.setWeek(d.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
				
				//LocalDate today = LocalDate.now();
				//System.out.println(d+": "+today.compareTo(d));
				
				empWSHrepo.save(wsh);
			}
			model.addAttribute("message", "Successfully added work schedule for " + wsh.getFull_name());
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
		}

		return new ModelAndView("theme/ajaxResponse.html", model);
	}

	@RequestMapping(value = "/saveQuickTimeReport", method = RequestMethod.POST)
	public ModelAndView saveQuickTimeReport(@ModelAttribute EmpTimeReportDTO av, ModelMap model, Authentication auth,
			final HttpServletRequest request) {
		try {
			User creator = (User) auth.getPrincipal();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate fromLocalDate = LocalDate.parse(av.getFrom_date(), formatter);
			LocalDate toLocalDate = LocalDate.parse(av.getTo_date(), formatter);
			List<LocalDate> dates = wSHservice.getDatesBetween(fromLocalDate, toLocalDate);

			LocalTime l1 = null, l2 = null;
			
			if (av.getWork_start()!=null&&av.getWork_end()!=null) {
				l1 = LocalTime.parse(av.getWork_start());
				l2 = LocalTime.parse(av.getWork_end());
				av.setWork_minute((Duration.between(l1, l2).toMinutes()) - av.getLunch_hour());
			}
			
			String msg="";

			for (LocalDate d : dates) {
				av.setUserid(creator.getId());
				av.setFull_name(creator.getFirstName()+" "+creator.getLastName());
				av.setDate(d);
				av.setAv_id(wSHservice.generateWavID(av));
				try {
					long wrkmin= (Duration.between(l1, l2).toMinutes()) - av.getLunch_hour();
					long hours = wrkmin / 60; 
					long minutes = wrkmin % 60;				
					av.setWork_minute(wrkmin);
					av.setWork_hour(hours+" H "+minutes+" Min");
				} catch (Exception e) {
					av.setWork_minute(0);
					av.setWork_hour("0 H 0 Min");
				}
				Date xdate = new SimpleDateFormat("yyyy-M-d").parse(av.getDate().toString());
				av.setDay(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(xdate));
				av.setWeek(d.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
				
				if(LocalDate.now().compareTo(d)<0 && av.getStatus()==1) {
					msg+="\n"+d+": Cant set worked hours for advance date.";
				}else {
					avrepo.save(av);
				}
				
			}
			model.addAttribute("message", "Successfully time reported for " + av.getFull_name()+msg);
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
		}

		return new ModelAndView("theme/ajaxResponse.html", model);
	}
	
	@ResponseBody
	@RequestMapping(value = "/saveDateTimeReport", method = RequestMethod.POST)
	public String saveDateTimeReport(@ModelAttribute EmpTimeReportDTO av, Authentication auth,
			final HttpServletRequest request) {
		String msg="";
		User user = (User) auth.getPrincipal();
		av.setUserid(user.getId());
		av.setFull_name(user.getFirstName()+" "+user.getLastName());
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate avDate = LocalDate.parse(av.getFrom_date(), formatter);

			LocalTime l1 = null, l2 = null;
			
			if (av.getWork_minute() > 0) {
				l1 = LocalTime.parse(av.getWork_start());
				l2 = LocalTime.parse(av.getWork_end());
			}else {
				av.setWork_start(null);
				av.setWork_end(null);
				av.setLunch_hour(0);
				av.setWork_minute(av.getWork_minute());
			}
			
			
			
			av.setDate(avDate);
			av.setAv_id(wSHservice.generateWavID(av));

			Date xdate = new SimpleDateFormat("yyyy-M-d").parse(av.getDate().toString());
			av.setDay(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(xdate));
			av.setWeek(avDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
			
			if(LocalDate.now().compareTo(avDate)<0 && av.getStatus()==1) {
				msg="Cant report for advance date: "+avDate;
			}else {
				avrepo.save(av);
				msg="Successfully reported for date: " + av.getFrom_date();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}

		return msg;
	}
	
	@ResponseBody
	@RequestMapping(value = "/saveRegularTime", method = RequestMethod.POST)
	public String saveRegularTime(@ModelAttribute EmpTimeReportDTO av, Authentication auth,
			final HttpServletRequest request) {
		String msg="";
		User user = (User) auth.getPrincipal();
		av.setUserid(user.getId());
		av.setFull_name(user.getFirstName()+" "+user.getLastName());
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate avDate = LocalDate.parse(av.getFrom_date(), formatter);

			LocalTime l1 = null, l2 = null;
			
			if (av.getWork_minute() > 0) {
				l1 = LocalTime.parse(av.getWork_start());
				l2 = LocalTime.parse(av.getWork_end());
			}else {
				av.setWork_start(null);
				av.setWork_end(null);
				av.setLunch_hour(0);
				av.setWork_minute(av.getWork_minute());
			}
			
			
			
			av.setDate(avDate);
			av.setAv_id(wSHservice.generateWavID(av));

			Date xdate = new SimpleDateFormat("yyyy-M-d").parse(av.getDate().toString());
			av.setDay(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(xdate));
			av.setWeek(avDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
			
			if(LocalDate.now().compareTo(avDate)<0 && av.getStatus()==1) {
				msg="Cant report for advance date: "+avDate;
			}else {
				avrepo.save(av);
				msg="Successfully reported for date: " + av.getFrom_date();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}

		return msg;
	}
	
	@ResponseBody
	@RequestMapping(value = "/saveOvertime", method = RequestMethod.POST)
	public String saveOvertime(@ModelAttribute Timereport_Overtime_emp av, Authentication auth,
			final HttpServletRequest request) {
		String msg="";
		User user = (User) auth.getPrincipal();
		av.setUserid(user.getId());
		av.setFull_name(user.getFirstName()+" "+user.getLastName());
		try {
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate avDate = LocalDate.parse(av.getFrom_date(), formatter);
			
			av.setDate(avDate);
			Date xdate = new SimpleDateFormat("yyyy-M-d").parse(av.getDate().toString());
			
			EmpTimeReportDTO etd = new EmpTimeReportDTO();
			try {
				etd= avrepo.findById(av.getAv_id()).get();
			}catch (Exception e) {
				/*
				etd.setAv_id(av.getAv_id());
				etd.setDate(avDate);
				etd.setDay(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(xdate));
				etd.setWeek(avDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
				etd.setStatus(5);
				etd.setWork_minute(0);
				etd.setUserid(av.getUserid());
				etd.setFull_name(av.getFull_name());
				etd.setWork_hour("0 H 0 Min");
				etd.setWork_desc("Overtime only");
				avrepo.save(etd);
				*/
			}			
			
			
			av.setOb_id(wSHservice.generateOBID(av,av.getObno()));

			
			av.setDay(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(xdate));
			av.setWeek(avDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
			
			if(LocalDate.now().compareTo(avDate)<0) {
				msg="Cant report for advance date: "+avDate;
			}else {
				obrepo.save(av);
				msg="Successfully reported for date: " + av.getFrom_date();
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg= e.getMessage();
		}

		return msg;
	}

	@GetMapping("/checkwshdate")
	public ModelAndView checkwshdate(@RequestParam("empid") Long empid, @RequestParam("fromdate") Long fromdate,
			@RequestParam("todate") Long todate, final ModelMap model) {

		return new ModelAndView("theme/ajaxResponse.html", model);
	}

	

	@GetMapping("/timeReportHistory")
	public ModelAndView timeReportHistory(@RequestParam(required = false) String from_date,
			@RequestParam(required = false) String to_date, @RequestParam(required = false) String empid,
			Authentication auth, final ModelMap model) {

		User user = (User) auth.getPrincipal();
		long userid = 0;
		if (empid == null) {
			userid = user.getId();
		} else {
			userid = Long.parseLong(empid);
		}

		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(userid);
		model.addAttribute("emp", empdtl);

		ArrayList<EmpTimeReportDTO> ewsh = null;
		if (from_date == null) {
			ewsh = avrepo.getAvailablityByuseridandDatelessthanToday(userid);
		} else {
			ewsh = avrepo.getAllBetweenDates(userid, from_date, to_date);
		}

		long total_wh = 0;
		for (EmpTimeReportDTO w : ewsh) {
			total_wh += w.getWork_minute();
		}
		model.addAttribute("wsh", ewsh);
		model.addAttribute("total_days", ewsh.size());
		model.addAttribute("total_work", wSHservice.mintsTOHmConvert(total_wh));
		model.addAttribute("emps", employeeService.getEmployeeList());
		return new ModelAndView("ems/pages/timeReportHistory", model);
	}
	
	@GetMapping("/reportWorkTimeHome")
	public ModelAndView reportWorkTimeHome(Authentication auth, final ModelMap model) {

		User user = (User) auth.getPrincipal();
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(user.getId());
		model.addAttribute("userdtl", empdtl);
		return new ModelAndView("ems/pages/timeReport/reportWorkTimeHome", model);
	}
	
	@ResponseBody
	@PostMapping("/rgTimeAction")
	private String rgTimeAction(Authentication auth,@RequestPayload String rg, @RequestPayload boolean ismail, @RequestPayload boolean decision) {
		String msg="Time report ";
		Gson gson = new Gson(); 
		EmpTimeReportDTO data = gson.fromJson(rg, EmpTimeReportDTO.class);
		User creator = (User) auth.getPrincipal();
		EmpTimeReportDTO av = avrepo.findById(data.getAv_id()).get();
		
		av.setStatus(data.getStatus());
		av.setWork_start(data.getWork_start());
		av.setWork_end(data.getWork_end());
		av.setLunch_hour(data.getLunch_hour());
		av.setWork_minute(data.getWork_minute());
		av.setWork_hour(data.getWork_hour());
		av.setWork_desc(data.getWork_desc());
		
		String emailText = "";
		if(decision) {
			msg+= "approved for ";
			emailText ="You time report for date:"+av.getDate()+" has been approved for the upcoming salary!";
			av.setIsapproved(true);
			av.setIsrejected(false);			
		}else {
			msg+= "rejected for ";
			emailText ="You time report for date:"+av.getDate()+" has been rejected!";
			av.setIsapproved(false);
			av.setIsrejected(true);			
		}	
		
		try {			
			avrepo.save(av);
			msg+= av.getFull_name();
			
			if(ismail) {
				// send mail
				SimpleMailMessage email = new SimpleMailMessage();
				email.setTo(userService.getUserByID(av.getUserid()).get().getEmail());
				email.setSubject("STATUS OF YOUR REPORTED TIME");
				final String appUrl = "https://ems.netlit.se";				
				email.setText(emailText);
				email.setFrom(env.getProperty("support.email"));
				try {
					mailSender.send(email);
				} catch (Exception e) {
					msg+="\nUnable to send mail.";
				}
			}			
			
		} catch (Exception e) {
			msg=e.getMessage();
		}
		return msg;
	}
	
	@ResponseBody
	@PostMapping("/obTimeAction")
	private String obTimeAction(Authentication auth,@RequestPayload String rg, @RequestPayload boolean ismail, @RequestPayload boolean decision) {
		String msg="OverTime report ";
		Gson gson = new Gson(); 
		Timereport_Overtime_emp data = gson.fromJson(rg, Timereport_Overtime_emp.class);
		User creator = (User) auth.getPrincipal();
		Timereport_Overtime_emp av =obrepo.findById(data.getOb_id()).get();
		
		av.setStatus(data.getStatus());
		av.setWork_start(data.getWork_start());
		av.setWork_end(data.getWork_end());
		av.setLunch_hour(data.getLunch_hour());
		av.setWork_minute(data.getWork_minute());
		av.setWork_hour(data.getWork_hour());
		av.setWork_desc(data.getWork_desc());
		
		String emailText = "";
		if(decision) {
			msg+= "approved for ";
			emailText ="You time report for date:"+av.getDate()+" has been approved for the upcoming salary!";
			av.setIsapproved(true);
			av.setIsrejected(false);			
		}else {
			msg+= "rejected for ";
			emailText ="You time report for date:"+av.getDate()+" has been rejected!";
			av.setIsapproved(false);
			av.setIsrejected(true);			
		}	
		
		try {			
			obrepo.save(av);
			msg+= av.getFull_name();
			if(ismail) {
				// send mail
				SimpleMailMessage email = new SimpleMailMessage();
				email.setTo(userService.getUserByID(av.getUserid()).get().getEmail());
				email.setSubject("STATUS OF YOUR REPORTED TIME");
				final String appUrl = "https://ems.netlit.se";				
				email.setText(emailText);
				email.setFrom(env.getProperty("support.email"));
				try {
					mailSender.send(email);
				} catch (Exception e) {
					msg+="\nUnable to send mail.";
				}
			}
		} catch (Exception e) {
			msg=e.getMessage();			
		}
		return msg;
	}

	@ResponseBody
	@GetMapping("/timereport/getbydate")
	public EmpTimeReportDTO getTimereports(@RequestParam String date, Authentication auth){
		User user = (User) auth.getPrincipal();
		EmpTimeReportDTO tr= new EmpTimeReportDTO();
		tr=avrepo.findbyOnedateEmpid(user.getId(), LocalDate.parse(date));
		return tr;		
	}
	
	@SuppressWarnings("null")
	@GetMapping("/getTimeFormsBydate")
	public ModelAndView getTimeFormsBydate(@RequestParam String from_date,@RequestParam String to_date, Authentication auth, final ModelMap model){
		User user = (User) auth.getPrincipal();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate fromLocalDate = LocalDate.parse(from_date, formatter);
		LocalDate toLocalDate = LocalDate.parse(to_date, formatter);
		List<LocalDate> dates = wSHservice.getDatesBetween(fromLocalDate, toLocalDate);		
		EmpTimeReportDTO tr= new EmpTimeReportDTO();
		List<EmpTimeReportDTO> trlist=new LinkedList<EmpTimeReportDTO>();
		
		for(LocalDate d:dates) {
			tr= getTimereportsByDate(d.toString(),user.getId());
			if(tr==null) {
				tr= new EmpTimeReportDTO();				
				tr.setStatus(1);
				tr.setDate(d);
				tr.setWork_start("08:00");
				tr.setWork_end("16:45");
				tr.setLunch_hour(45);
				tr.setWork_minute(480);
				tr.setWork_hour("8 H 0 Min");
				tr.setUserid(user.getId());
				tr.setAv_id(wSHservice.generateWavID(tr));
				
				List<Timereport_Overtime_emp> overtime = new ArrayList<Timereport_Overtime_emp>();
				for(int i=1;i<3;i++) {
					Timereport_Overtime_emp ovemp= new Timereport_Overtime_emp(user.getId(),d);			
					
					try {
						ovemp= obrepo.findById(wSHservice.generateOBID(ovemp,i)).get();
						ovemp.setBg_class( ((ovemp.isIsapproved()) ? "bg-success" : "bg-warning"));				
						if(ovemp.isIsrejected())ovemp.setBg_class("bg-danger");
					}catch (Exception e) {
						ovemp.setOb_id(wSHservice.generateOBID(ovemp,i));
						ovemp.setWork_minute(0);
						ovemp.setWork_hour("0 H 0 Min");
						ovemp.setObno(i);
					}								
					overtime.add(ovemp);
				}
				tr.setOvertime(overtime);	
				
			}else {
				List<Timereport_Overtime_emp> overtime = tr.getOvertime();
				for(int i=1;i<3;i++) {
					Timereport_Overtime_emp ovemp = new Timereport_Overtime_emp(user.getId(),d);
					try {
						ovemp= overtime.get(i-1);
						ovemp.setObno(i);
						ovemp.setBg_class( ((ovemp.isIsapproved()) ? "bg-success" : "bg-warning"));				
						if(ovemp.isIsrejected())ovemp.setBg_class("bg-danger");
						//overtime.add(ovemp);
					}catch (Exception e) {
						ovemp.setOb_id(wSHservice.generateOBID(ovemp,i));
						ovemp.setWork_minute(0);
						ovemp.setWork_hour("0 H 00 Min");						
						ovemp.setObno(i);
						overtime.add(ovemp);
					}
				}
				tr.setOvertime(overtime);
				
				
				tr.setBg_class( ((tr.isIsapproved()) ? "bg-success" : "bg-warning"));				
				if(tr.isIsrejected())tr.setBg_class("bg-danger");
			}			
					
			trlist.add(tr);
		}
		
		model.addAttribute("avtype", availabilityRepo.findAll());
		model.addAttribute("obtype", overtimeRepo.findAll());
		model.addAttribute("trlist", trlist);
		return	new ModelAndView("ems/ajaxResponse/timereport/gettimereportforms",model);	
	}
	
	public EmpTimeReportDTO getTimereportsByDate(String date,long userid){
		EmpTimeReportDTO tr= new EmpTimeReportDTO();
		tr=avrepo.findbyOnedateEmpidanytype(userid, LocalDate.parse(date));
		return tr;		
	}
}

class TotalTRDTO {
	String total_rg;
	String total_ob;
	public String getTotal_rg() {
		return total_rg;
	}
	public void setTotal_rg(String total_rg) {
		this.total_rg = total_rg;
	}
	public String getTotal_ob() {
		return total_ob;
	}
	public void setTotal_ob(String total_ob) {
		this.total_ob = total_ob;
	}
	@Override
	public String toString() {
		return total_rg + "," + total_ob;
	}	
	
}
