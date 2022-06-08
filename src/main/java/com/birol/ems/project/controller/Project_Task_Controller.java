package com.birol.ems.project.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.birol.ems.dao.EmpTimeReportRepo;
import com.birol.ems.dao.EmpWSHrepo;
import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.dto.EmpTimeReportDTO;
import com.birol.ems.project.dao.ProjectDao;
import com.birol.ems.project.dao.Project_ActivityDao;
import com.birol.ems.project.dao.Project_TaskDao;
import com.birol.ems.project.dto.Project;
import com.birol.ems.project.dto.Project_Activity;
import com.birol.ems.project.dto.Project_Task;
import com.birol.ems.service.EmployeeService;
import com.birol.ems.service.WSHservice;
import com.birol.persistence.model.User;

@Controller
public class Project_Task_Controller {
	
	@Autowired
	private Project_TaskDao project_TaskDao;
	@Autowired
	private Project_ActivityDao project_ActivityDao;
	@Autowired
	private ProjectDao projectDao;
	@Autowired
	EmployeeService employeeService;
	@Autowired
	WSHservice wSHservice;
	@Autowired
	EmpWSHrepo empWSHrepo;
	@Autowired
	EmpTimeReportRepo avrepo;
	
	@RequestMapping(value = "/createTask", method = RequestMethod.POST)
	public ModelAndView createTask(@ModelAttribute Project_Task pt, ModelMap model, Authentication auth,
			final HttpServletRequest request) {
		User user = (User) auth.getPrincipal();		
		try {
			if (pt.getAttachment_m().getSize() > 0)
				pt.setAttachment(pt.getAttachment_m().getBytes());
			pt.setCreatorid(user.getId());
			pt.setCreatorname(user.getFirstName()+" "+user.getLastName());
			pt.setStatus("New");
			project_TaskDao.save(pt);
			
			Project_Activity activity = new Project_Activity();
			activity.setMessage(pt.getAssigneename()+ " is assigned to a task by "+user.getFirstName()+" "+user.getLastName());
			activity.setCreatorid(user.getId());
			activity.setCreatorname(user.getFirstName()+" "+user.getLastName());
			activity.setProjectid(pt.getProjectid());
			activity.setType("TASK");
			project_ActivityDao.save(activity);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String redirurl= "viewProject?projectid="+pt.getProjectid();
		return new ModelAndView("redirect:/"+redirurl, model);
	}
	
	@RequestMapping(value = "/editTaskDo", method = RequestMethod.POST)
	public ModelAndView editTaskDo(@ModelAttribute Project_Task pt, ModelMap model, Authentication auth,
			final HttpServletRequest request) {
		User user = (User) auth.getPrincipal();		
		try {
			Project_Task et= project_TaskDao.findBytaskid(pt.getTaskid());
			
			if (pt.getAttachment_m().getSize() > 0)
				pt.setAttachment(pt.getAttachment_m().getBytes());
			pt.setCreatorid(user.getId());
			pt.setCreatorname(user.getFirstName()+" "+user.getLastName());
			pt.setCreated(et.getCreated());
			project_TaskDao.save(pt);
			
			if(pt.getAssigneeid()!=et.getAssigneeid()) {
				Project_Activity activity = new Project_Activity();
				activity.setMessage(pt.getAssigneename()+ " is assigned to a task by "+user.getFirstName()+" "+user.getLastName());
				activity.setCreatorid(user.getId());
				activity.setCreatorname(user.getFirstName()+" "+user.getLastName());
				activity.setProjectid(pt.getProjectid());
				activity.setType("TASK");
				project_ActivityDao.save(activity);
			}		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String redirurl= "viewProject?projectid="+pt.getProjectid();
		return new ModelAndView("redirect:/"+redirurl, model);
	}
	
	@GetMapping("/deleteTaskDo")
	public ModelAndView deleteTaskDo(@RequestParam("taskid") Long taskid, ModelMap model) {
		Project_Task task= project_TaskDao.findBytaskid(taskid);
		project_TaskDao.delete(task);		
		String redirurl= "viewProject?projectid="+task.getProjectid();
		return new ModelAndView("redirect:/"+redirurl, model);
	}

	@GetMapping("/editTask")
	public ModelAndView editTask(@RequestParam("taskid") Long taskid, final ModelMap model) {
		Project_Task task= project_TaskDao.findBytaskid(taskid);
		Project project= projectDao.findProjectByprojectid(task.getProjectid()).get();
		model.addAttribute("task", task);
		model.addAttribute("project", project);
		return new ModelAndView("ems/content/project/taskEdit", model);
	}
	
	@GetMapping("/updateTaskStatus")
	@ResponseBody
	public String updateTaskStatus(@RequestParam("taskid") Long taskid, @RequestParam("status") String status,
			final ModelMap model) {
		Project_Task task = project_TaskDao.findBytaskid(taskid);
		task.setStatus(status);
		project_TaskDao.save(task);
		return "Status updated for Task #" + taskid;
	}

	@GetMapping("/updateTaskType")
	@ResponseBody
	public String updateTaskType(@RequestParam("taskid") Long taskid, @RequestParam("type") String type,
			final ModelMap model) {
		Project_Task task = project_TaskDao.findBytaskid(taskid);
		task.setType(type);
		project_TaskDao.save(task);
		return "Type updated for Task #" + taskid;
	}

	@GetMapping("/updateTaskPriority")
	@ResponseBody
	public String updateTaskPriority(@RequestParam("taskid") Long taskid, @RequestParam("priority") String priority,
			final ModelMap model) {
		Project_Task task = project_TaskDao.findBytaskid(taskid);
		task.setPriority(priority);
		project_TaskDao.save(task);
		return "Priority updated for Task #" + taskid;
	}
	
	@GetMapping("/viewTask")
	public ModelAndView viewTask(@RequestParam("taskid") Long taskid, final ModelMap model) {
		Project_Task task= project_TaskDao.findBytaskid(taskid);
		Project project= projectDao.findProjectByprojectid(task.getProjectid()).get();
		model.addAttribute("task", task);
		model.addAttribute("project", project);
		return new ModelAndView("ems/content/project/taskView", model);
	}
	
	@GetMapping("/download/task/attachment")
	public void downloadUsersFile(@RequestParam Long taskid, HttpServletResponse resp, Principal principal)
			throws IOException {

		Project_Task task = project_TaskDao.findBytaskid(taskid);
		byte[] byteArray = null;
		String ext = ".pdf";

		byteArray = task.getAttachment();
		InputStream is = new ByteArrayInputStream(byteArray);
		String mimeType = URLConnection.guessContentTypeFromStream(is);
		if (mimeType != null) {
			ext = "." + mimeType.split("/")[1];
		}
		System.out.println(mimeType);

		resp.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM.getType());
		resp.setHeader("Content-Disposition", "attachment; filename=Task" + task.getTaskid() + ext);
		resp.setContentLength(byteArray.length);
		OutputStream os = resp.getOutputStream();
		try {
			os.write(byteArray, 0, byteArray.length);
		} finally {
			os.close();
		}
	}
	
	@GetMapping("/timereporttask")
	public ModelAndView timereporttask(@RequestParam("empid") Long empid,@RequestParam("taskid") Long taskid, final ModelMap model) {
		EMPLOYEE_BASIC empdtl = employeeService.getEmployeebyID(empid);
		Project_Task task= project_TaskDao.findBytaskid(taskid);
		model.addAttribute("userdtl", empdtl);
		model.addAttribute("task", task);
		return new ModelAndView("ems/ajaxResponse/setavailablitymodaltask", model);
	}
	
	@ResponseBody
	@RequestMapping(value = "/saveTimeReportTask", method = RequestMethod.POST)
	public String saveTimeReportTask(@ModelAttribute EmpTimeReportDTO av, ModelMap model, Authentication auth,
			final HttpServletRequest request) {
		String message= "";
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate date = LocalDate.parse(av.getFrom_date(), formatter);
			LocalTime l1 = null, l2 = null;
			if (av.getStatus() == 1) {
				l1 = LocalTime.parse(av.getWork_start());
				l2 = LocalTime.parse(av.getWork_end());
			}

			String msg = "";

			av.setDate(date);
			av.setAv_id(wSHservice.generateWavID(av));
			try {
				av.setWork_minute((Duration.between(l1, l2).toMinutes()) - av.getLunch_hour());
			} catch (Exception e) {
				av.setWork_minute(0);
			}
			Date xdate = new SimpleDateFormat("yyyy-M-d").parse(date.toString());
			av.setDay(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(xdate));
			av.setWeek(date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));

			if (LocalDate.now().compareTo(date) < 0 && av.getStatus() == 1) {
				message = date + ": Cant set for advance date.";
			} else {
				avrepo.save(av);
				message= "Successfully time reported for Task #" + av.getTaskid() ;
			}			
			
			model.addAttribute("message", message);
		} catch (Exception e) {
			message=e.getMessage();
			model.addAttribute("message", e.getMessage());
		}

		return message;
	}
}

