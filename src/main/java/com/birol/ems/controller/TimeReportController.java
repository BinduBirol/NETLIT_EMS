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
import java.util.Optional;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.birol.ems.dao.EmpTimeReportRepo;
import com.birol.ems.dao.EmpWSHrepo;
import com.birol.ems.dto.EmpTimeReportDTO;
import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.dto.Time_report_approved;
import com.birol.ems.dto.LoggedinUserDTO;
import com.birol.ems.repo.EmployeeRepository;
import com.birol.ems.service.EmployeeService;
import com.birol.ems.service.WSHservice;
import com.birol.persistence.model.User;

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
		model.addAttribute("emps", employeeService.getEmployeeList());
		model.addAttribute("emp", empdtl);

		ArrayList<EmpTimeReportDTO> availist = new ArrayList<EmpTimeReportDTO>();

		if (empid == null & from_date == null) {
			availist = avrepo.getAvailablityAllandDateToday();
		} else if (empid.isEmpty() & from_date != null) {
			availist = avrepo.getAllusersBetweenDates(from_date, to_date);
		} else if (!empid.isEmpty() && empid != null) {
			availist = avrepo.getAllBetweenDates(Long.parseLong(empid), from_date, to_date);
		}

		model.addAttribute("wsh", availist);

		return new ModelAndView("ems/pages/empsTimereport", model);
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
		model.addAttribute("userdtl", empdtl);
		return new ModelAndView("ems/ajaxResponse/setavailablitymodal", model);
	}

	@GetMapping("/workschedulehistory")
	public ModelAndView workschedulehistory(@RequestParam("empid") Long empid, final ModelMap model) {
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(empid);
		ArrayList<Time_report_approved> empwsh = new ArrayList<Time_report_approved>();
		empwsh = (ArrayList<Time_report_approved>) empWSHrepo.findByUserid(empid);
		model.addAttribute("emp", empdtl);
		model.addAttribute("wsh", empwsh);
		return new ModelAndView("ems/ajaxResponse/viewWorkShHistory", model);
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

	@RequestMapping(value = "/saveTimeReport", method = RequestMethod.POST)
	public ModelAndView saveTimeReport(@ModelAttribute EmpTimeReportDTO av, ModelMap model, Authentication auth,
			final HttpServletRequest request) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate fromLocalDate = LocalDate.parse(av.getFrom_date(), formatter);
			LocalDate toLocalDate = LocalDate.parse(av.getTo_date(), formatter);
			List<LocalDate> dates = wSHservice.getDatesBetween(fromLocalDate, toLocalDate);

			LocalTime l1 = null, l2 = null;
			if (av.getStatus() == 1) {
				l1 = LocalTime.parse(av.getWork_start());
				l2 = LocalTime.parse(av.getWork_end());
			}
			
			String msg="";

			for (LocalDate d : dates) {
				av.setDate(d);
				av.setAv_id(wSHservice.generateWavID(av));
				try {
					av.setWork_minute((Duration.between(l1, l2).toMinutes()) - av.getLunch_hour());
				} catch (Exception e) {
					av.setWork_minute(0);
				}
				Date xdate = new SimpleDateFormat("yyyy-M-d").parse(av.getDate().toString());
				av.setDay(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(xdate));
				av.setWeek(d.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
				
				if(LocalDate.now().compareTo(d)<0) {
					msg+="\n"+d+": Cant set for advance date.";
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

	@GetMapping("/checkwshdate")
	public ModelAndView checkwshdate(@RequestParam("empid") Long empid, @RequestParam("fromdate") Long fromdate,
			@RequestParam("todate") Long todate, final ModelMap model) {

		return new ModelAndView("theme/ajaxResponse.html", model);
	}

	@GetMapping("/viewtimeReport")
	public ModelAndView viewtimeReport(@RequestParam(required = false) String from_date,
			@RequestParam(required = false) String to_date, @RequestParam(required = false) String c,
			@RequestParam(required = false) String empid, Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();
		long userid = 0;
		if (empid == null) {
			userid = user.getId();
		} else {
			userid = Long.parseLong(empid);
		}

		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(userid);
		model.addAttribute("emp", empdtl);

		ArrayList<Time_report_approved> ewsh = null;
		if(from_date==null) {
			ewsh = empWSHrepo.getToday(userid);
		}else {
			ewsh = empWSHrepo.getAllBetweenDates(userid, from_date, to_date);
		}
		
		long total_wh = 0;
		for (Time_report_approved w : ewsh) {
			total_wh += w.getWork_minute();
		}
		model.addAttribute("wsh", ewsh);
		model.addAttribute("total_days", ewsh.size());
		model.addAttribute("total_work", wSHservice.mintsTOHmConvert(total_wh));
		model.addAttribute("emps", employeeService.getEmployeeList());
		return new ModelAndView("ems/pages/viewtimeReport", model);
	}

	@GetMapping("/ReportWorkTime")
	public ModelAndView reportWorkTime(@RequestParam(required = false) String from_date,
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
		return new ModelAndView("ems/pages/ReportWorkTime", model);
	}

	@GetMapping("/approveAvailablity")
	@ResponseBody
	public String ApproveAvailablity(Authentication auth, @RequestParam String av_id, @RequestParam String start,
			@RequestParam String end, @RequestParam int lbreak, @RequestParam int minutes,
			@RequestParam boolean approve, @RequestParam String wdesc, final ModelMap model) {

		User creator = (User) auth.getPrincipal();
		EmpTimeReportDTO av = avrepo.findById(av_id).get();
		Time_report_approved wsh = new Time_report_approved();

		wsh.setAssigned_by_full_name(creator.getFirstName() + " " + creator.getLastName());
		wsh.setAssigned_by_id(creator.getId());

		wsh.setDate(av.getDate());
		wsh.setDay(av.getDay());
		wsh.setWeek(av.getWeek());
		wsh.setWork_start(start);
		wsh.setWork_end(end);
		wsh.setLunch_hour(lbreak);
		wsh.setWork_minute(minutes);
		wsh.setUserid(av.getUserid());
		wsh.setFull_name(av.getFull_name());
		wsh.setAvailability_id(av.getAv_id());
		String wshid = wSHservice.generateWShID(wsh);
		wsh.setStatus(av.getStatus());
		wsh.setWork_desc(wdesc);
		wsh.setWork_sh_id(wshid);

		av.setWork_start(start);
		av.setWork_end(end);
		av.setLunch_hour(lbreak);
		av.setWork_minute(minutes);
		av.setWork_desc(wdesc);
		av.setIsapproved(approve);
		avrepo.save(av);
		if (approve) {
			empWSHrepo.save(wsh);
		} else {
			empWSHrepo.deleteById(wshid);
		}
		model.addAttribute("message", approve);
		return av_id;
	}

}
