package com.birol.ems.timereport.re;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.repo.EmployeeRepository;
import com.birol.ems.service.EmployeeService;
import com.birol.ems.service.WSHservice;
import com.birol.persistence.model.User;
import com.birol.service.IUserService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
		
		TreeMap<LocalDate, ArrayList<Time_Report_DTO>> map= new TreeMap<LocalDate, ArrayList<Time_Report_DTO>>();
		
		for(LocalDate d: dates) {
			ArrayList<Time_Report_DTO> trlist = new ArrayList<Time_Report_DTO>();			
			
			if(emp_id.equals("all")) {
				trlist = new ArrayList<Time_Report_DTO>();				
				trlist= time_Report_Repo.findByDateAndIsapprovedAndIsrejected(d,false,false);
			}else if(emp_id.equals("me")) {
				trlist = new ArrayList<Time_Report_DTO>();	
				for(EMPLOYEE_BASIC x: myemps) {
					
					trlist.addAll(time_Report_Repo.findByDateAndEmpidAndIsapprovedAndIsrejected(d,x.getEmpid(),false,false));					
				}
													
			}else {
				trlist = new ArrayList<Time_Report_DTO>();
				trlist= time_Report_Repo.findByDateAndEmpidAndIsapprovedAndIsrejected(d,Long.parseLong(emp_id.split("-")[1]),false,false);
			}		
			
			if(trlist.size()>0)map.put(d, trlist);
			
		}
		
		model.addAttribute("trtypeALL", timeReportTypesRepo.findAll());
		model.addAttribute("trtypeRG", timeReportTypesRepo.findByisOB(false));
		model.addAttribute("trtypeOB", timeReportTypesRepo.findByisOB(true));
		model.addAttribute("map", map);
		
		return new ModelAndView("ems/ajaxResponse/timereport/getPendingTimeReports", model);	
	}
	
	@ResponseBody
	@PostMapping("/doPendingTimeReportAction")
	private String doPendingTimeReportAction (Authentication auth,@RequestPayload String rg, 
			@RequestPayload boolean ismail, @RequestPayload boolean decision) {
		String msg="";
		Type listType = new TypeToken<ArrayList<Time_Report_DTO>>(){}.getType();
		List<Time_Report_DTO> dataList = new Gson().fromJson(rg, listType);
		
		User creator = (User) auth.getPrincipal();		
		
		if (decision) {
			msg = "Approved "+dataList.size()+" time reports.";
		} else {
			msg = "Rejected "+dataList.size()+" time reports.";
		}
		for(Time_Report_DTO data: dataList) {
			Time_Report_DTO av = time_Report_Repo.findById(data.getTr_id()).get();
			String chief= employeeRepository.findbyEmpid(av.getEmpid()).getNearest_chief_name();
			av.setStatus(data.getStatus());
			av.setWork_start(data.getWork_start());
			av.setWork_end(data.getWork_end());
			av.setWork_interval(data.getWork_interval());
			av.setWork_minute(data.getWork_minute());
			av.setWork_hour(data.getWork_hour());
			av.setWork_desc(data.getWork_desc());
			
			String emailText = "";
			if(decision) {				
				emailText ="You time report for date:"+av.getDate()+" has been approved for the upcoming salary!";
				av.setIsapproved(true);
				av.setIsrejected(false);			
			}else {
				
				emailText ="You time report for date:"+av.getDate()+" has been rejected! For further enquiry contact your nearest chief ["+chief+"].";
				av.setIsapproved(false);
				av.setIsrejected(true);			
			}	
			
			try {			
				time_Report_Repo.save(av);	
				//System.out.println(emailText);
				if(ismail) {					
					// send mail
					String tomail= userService.getUserByID(av.getEmpid()).get().getEmail();
					SimpleMailMessage email = new SimpleMailMessage();
					email.setTo(tomail);
					email.setSubject("STATUS OF YOUR REPORTED TIME");
					final String appUrl = "https://ems.netlit.se";				
					email.setText(emailText);
					email.setFrom(env.getProperty("support.email"));
					try {
						mailSender.send(email);
					} catch (Exception e) {
						msg+="\n[Unable to send mail to "+tomail+"]";
					}
				}			
				
			} catch (Exception e) {
				msg=e.getMessage();
			}
		}
		
		return msg;
	}
	
	@ResponseBody
	@PostMapping("/doTimeReportDecision")
	private String doTimeReportDecision(Authentication auth,@RequestPayload String rg, 
			@RequestPayload boolean ismail, @RequestPayload boolean decision) {
		String msg="Time report ";
		Gson gson = new Gson(); 
		Time_Report_DTO data = gson.fromJson(rg, Time_Report_DTO.class);
		User creator = (User) auth.getPrincipal();
		Time_Report_DTO av = time_Report_Repo.findById(data.getTr_id()).get();
		
		av.setStatus(data.getStatus());
		av.setWork_start(data.getWork_start());
		av.setWork_end(data.getWork_end());
		av.setWork_interval(data.getWork_interval());
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
			time_Report_Repo.save(av);
			msg+= av.getFull_name();
			
			if(ismail) {
				// send mail
				SimpleMailMessage email = new SimpleMailMessage();
				email.setTo(userService.getUserByID(av.getEmpid()).get().getEmail());
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
	
	@PostMapping("/getApprovedTimeReports")
	public ModelAndView getApprovedTimeReports(	@RequestPayload String from_date,
												@RequestPayload String to_date,												
												Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();
		ArrayList<Time_Report_DTO> rglist= time_Report_Repo.findApprovedByEmpidAndFromDateToDate(user.getId(),from_date,to_date);
		
		HashSet<Integer> weeks= new HashSet<>();
		for(Time_Report_DTO r: rglist) weeks.add(r.getWeek());
		
		TreeMap<Integer, ArrayList<Time_Report_DTO>> rgmap= new TreeMap<>();
		HashMap<Integer, TotalTRDTO> totalmap= new HashMap<>();
		
		int tRwm=0;
		int tOwm=0;
		
		int gtRwm=0;
		int gtOwm=0;
		
		TotalTRDTO tobj= new TotalTRDTO();
		TotalTRDTO gtobj= new TotalTRDTO();
		
		for(int w: weeks) {
			tRwm=0;
			tOwm=0;
			tobj= new TotalTRDTO();
			ArrayList<Time_Report_DTO> rglist2 = new ArrayList<>();
			for (Time_Report_DTO r : rglist) {
				if (r.getWeek() == w) {
					rglist2.add(r);
				if(!r.getTrTypes().isOB) {tRwm += r.getWork_minute();gtRwm += r.getWork_minute();}
				if(r.getTrTypes().isOB) {tOwm += r.getWork_minute();gtOwm += r.getWork_minute();}
				}
			}
			
			tobj.setTotal_rg(wSHservice.mintsTOHmConvert(tRwm));
			tobj.setTotal_ob(wSHservice.mintsTOHmConvert(tOwm));
			totalmap.put(w, tobj);
			rgmap.put(w, rglist2);
		}
		gtobj.setTotal_rg(wSHservice.mintsTOHmConvert(gtRwm));
		gtobj.setTotal_ob(wSHservice.mintsTOHmConvert(gtOwm));
		
		model.addAttribute("rgmap", rgmap);
		model.addAttribute("totalmap", totalmap);
		model.addAttribute("grandtotal", gtobj);
		return new ModelAndView("ems/ajaxResponse/timereport/getApprovedTimeReports", model);	
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
