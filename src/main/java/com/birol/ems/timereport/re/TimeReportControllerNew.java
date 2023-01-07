package com.birol.ems.timereport.re;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
import org.springframework.messaging.handler.annotation.Payload;
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

import com.birol.ems.contract.email.Email_msg_DTO;
import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.repo.EmployeeRepository;
import com.birol.ems.service.EmployeeService;
import com.birol.ems.service.WSHservice;
import com.birol.persistence.model.User;
import com.birol.service.IUserService;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
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
	
	@ResponseBody
	@RequestMapping(value = "/saveVacationRequest", method = RequestMethod.POST)
	public ArrayList<String> saveVacationRequest(@ModelAttribute Time_Report_DTO av, ModelMap model, Authentication auth,
			final HttpServletRequest request) {
		ArrayList<String> response= new ArrayList<String>();
		try {
			User creator = (User) auth.getPrincipal();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate fromLocalDate = LocalDate.parse(av.getFrom_date(), formatter);
			LocalDate toLocalDate = LocalDate.parse(av.getTo_date(), formatter);
			List<LocalDate> dates = wSHservice.getDatesBetween(fromLocalDate, toLocalDate);			

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
					//do naything
				}else {					
					time_Report_Repo.save(av);									
					response.add(d.format(formatter));
				}					
			}
			
		} catch (Exception e) {
			response.add(e.getMessage());
			e.printStackTrace();
		}
		return response;
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
					TimeReportTypesDTO type= timeReportTypesRepo.findByTypename("Regular work time");
					tr.setNotfoundindb(true);
					tr.setWork_minute(0);
					tr.setWork_hour("0 H 00 Min");
					if(i==1) {
						tr.setStatus(type.getTypeid());
						tr.setWork_start(type.getStart());
						tr.setWork_end(type.getEnd());
						tr.setWork_interval(type.getInterval_minutes());
						tr.setWork_minute(480);
						tr.setWork_hour(wSHservice.mintsTOHmConvert(480));
					}					
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
		//final Gson gson = Converters.registerOffsetDateTime(new GsonBuilder()).create();		
		String msg="";
		User user = (User) auth.getPrincipal();		
		try {
			//System.out.println(str);
			Time_Report_DTO av= gson.fromJson(str, Time_Report_DTO.class);
			av.setEmpid(user.getId());
			av.setFull_name(user.getFirstName()+" "+user.getLastName());
			
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
	@PostMapping("/doPendingSelectedTimeReportAction")
	private String doPendingSelectedTimeReportAction (Authentication auth,@RequestPayload String rg, 
			@RequestPayload boolean ismail, @RequestPayload boolean decision) {
		String msg="";		
		List<Time_Report_DTO> dataList= new ArrayList<Time_Report_DTO>();
		List<Time_Report_DTO> dataListForMail= new ArrayList<Time_Report_DTO>();
		try {
			Type listType = new TypeToken<ArrayList<Time_Report_DTO>>(){}.getType();
			dataList = new Gson().fromJson(rg, listType);
		} catch (Exception e) {
			msg= e.getMessage();
			return msg;
		}
		
		User user = (User) auth.getPrincipal();		
		
		if (decision) {
			msg = "Approved "+dataList.size()+" time reports.";
		} else {
			msg = "Rejected "+dataList.size()+" time reports.";
		}
		for(Time_Report_DTO data: dataList) {
			Time_Report_DTO av = time_Report_Repo.findById(data.getTr_id()).get();
			av.setStatus(data.getStatus());
			av.setWork_start(data.getWork_start());
			av.setWork_end(data.getWork_end());
			av.setWork_interval(data.getWork_interval());
			av.setWork_minute(data.getWork_minute());
			av.setWork_hour(data.getWork_hour());
			av.setWork_desc(data.getWork_desc());			
			if(decision) {				
				av.setIsapproved(true);
				av.setIsrejected(false);			
			}else {
				av.setIsapproved(false);
				av.setIsrejected(true);
			}			
			try {
				time_Report_Repo.save(av);
				dataListForMail.add(av);
			} catch (Exception e) {msg=e.getMessage();}
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
			msg+= "approved for ";
			emailText ="You time report for date "+av.getDate()+" has been approved for the upcoming salary!";
			av.setIsapproved(true);
			av.setIsrejected(false);			
		}else {
			msg+= "rejected for ";
			emailText ="You time report for date "+av.getDate()+" has been rejected! For further enquiry contact your nearest chief ["+chief+"].";
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
	
	@GetMapping("/calendar")
	public ModelAndView calander(Authentication auth, ModelMap model) {
		model.addAttribute("types", timeReportTypesRepo.findAllByOrderByIsWorkingDescTypeidAsc());
		return new ModelAndView("ems/pages/calendar", model);
	}
	
	
	@ResponseBody
	@GetMapping("/getTimereportEventsForcalendar")
	public ArrayList<EventCalendarDTO> getTimereportEventsForcalendar(Authentication auth, ModelMap model) {
		User user = (User) auth.getPrincipal();
		ArrayList<Time_Report_DTO> tr= new ArrayList<Time_Report_DTO>();
		ArrayList<EventCalendarDTO> events= new ArrayList<EventCalendarDTO>();
		tr= time_Report_Repo.findByEmpid(user.getId());
		
		for(Time_Report_DTO t:tr) {
			EventCalendarDTO event= new EventCalendarDTO();			
			if(t.getWork_minute()>0) {
				LocalDateTime start= t.getDate().atTime(Integer.parseInt(t.getWork_start().split(":")[0]), Integer.parseInt(t.getWork_start().split(":")[1]));
				LocalDateTime end= t.getDate().atTime(Integer.parseInt(t.getWork_end().split(":")[0]), Integer.parseInt(t.getWork_end().split(":")[1]));
				event.setStart(String.valueOf(start));
				event.setEnd(String.valueOf(end));
			}else {
				event.setStart(String.valueOf(t.getDate().atTime(0,0)));
				event.setAllDay(true);
			}	
			event.setTitle(t.getTrTypes().getTypename());			
			event.setTypeid(t.getStatus());
			event.setStartHour(t.getWork_start());
			event.setEndHour(t.getWork_end());
			event.setBreakMin(t.getWork_interval());
			event.setTrid(t.getTr_id());
			event.setIsapproved(t.isIsapproved());
			event.setIsrejected(t.isIsrejected());
			event.setWork_hour(t.getWork_hour());
			event.setWorkdesc(t.getWork_desc());
			event.setWork_minute(t.getWork_minute());
			
			event.setColor("#2874A6");
			if(t.isIsapproved())event.setColor("#1D8348");
			if(t.isIsrejected())event.setColor("#FF3333");
			
			events.add(event);
		}	
		
		return events;
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = "/saveTimeReportCalander", method = RequestMethod.POST)
	public String saveTimeReportCalander(@ModelAttribute Time_Report_DTO av, Authentication auth,
			final HttpServletRequest request) {
		String msg="";
		User user = (User) auth.getPrincipal();
		try {
			av.setEmpid(user.getId());
			av.setFull_name(user.getFirstName()+" "+user.getLastName());
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate avDate = LocalDate.parse(av.getFrom_date(), formatter);
			Date xdate = new SimpleDateFormat("yyyy-M-d").parse(avDate.toString());
			av.setDate(avDate);
			av.setDay(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(xdate));
			av.setWeek(av.getDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
			
			ArrayList<Time_Report_DTO> existing= time_Report_Repo.findByDateAndEmpid(av.getDate(),av.getEmpid());			
			
			
			if(av.getTr_id().isEmpty()) {				
				if(existing.size()>=3) {msg="Can't report more than 3 times for same date";return msg;}
				String trid= new String(wSHservice.generateTimeReportID(av, existing.size()+1));
				av.setTr_id(trid);
				if(!validatebeforeNewSaveTR(av,existing)) {msg="Try a different type for "+av.getDate();return msg;}
			}	
			
			if(LocalDate.now().compareTo(avDate)<0 && av.getWork_minute()>0) {
				msg="Cant report for advance date: "+avDate;return msg;
			}else {
				if(!validatebeforeOldSaveTR(av,existing)) {msg="Try a different type for "+av.getDate();return msg;}	
				time_Report_Repo.save(av);
				msg="Successfully reported for date: " + av.getFrom_date();
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg= e.getMessage();
		}
		return msg;
		
	}
	
	
	@GetMapping("/timeReportTypesHome")
	public ModelAndView timeReportTypesHome(Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();
		ArrayList<TimeReportTypesDTO> types = timeReportTypesRepo.findAllByOrderByIsWorkingDescTypeidAsc();
		model.addAttribute("types", types);
		return new ModelAndView("ems/pages/timeReport/timeReportTypesHome", model);		
	}
	
	@PostMapping("/dotimeReportTypes")
	public ModelAndView dotimeReportTypes(Authentication auth, final ModelMap model,@ModelAttribute TimeReportTypesDTO type) {
		User user = (User) auth.getPrincipal();
		try {
			timeReportTypesRepo.save(type);
			model.addAttribute("msg", "TR type saved successfuly.");
		}catch (Exception e) {			
			model.addAttribute("msg", e.getMessage());
		}
		
		return new ModelAndView("redirect:/timeReportTypesHome", model);
	}
	
	@PostMapping("/deletetimeReportTypes")
	public ModelAndView deletetimeReportTypes(Authentication auth, final ModelMap model,@Payload int id) {
		User user = (User) auth.getPrincipal();	
		try {
			timeReportTypesRepo.deleteById(id);
			model.addAttribute("msg", "TR type deleted successfuly.");
		} catch (Exception e) {
			model.addAttribute("msg", "Can not delete this type. cause it is being used in the system.");
		}
		
		return new ModelAndView("redirect:/timeReportTypesHome", model);
	}
	
	
	
	
	private boolean validatebeforeNewSaveTR(Time_Report_DTO av, ArrayList<Time_Report_DTO> existing) {	
		boolean isrgache= false;
		for(Time_Report_DTO t: existing) {
			if(av.getStatus()==t.getStatus())return false;
			if(!t.getTrTypes().isOB())isrgache= true;
		}
		TimeReportTypesDTO type= timeReportTypesRepo.findById(av.getStatus()).get();		
		if(isrgache && !type.isOB())return false;
		return true;	
	}
	
	private boolean validatebeforeOldSaveTR(Time_Report_DTO av, ArrayList<Time_Report_DTO> existing) {	
		for(Time_Report_DTO t: existing) {			
			if(av.getStatus()==t.getStatus())return false;		
		}
		return true;
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
