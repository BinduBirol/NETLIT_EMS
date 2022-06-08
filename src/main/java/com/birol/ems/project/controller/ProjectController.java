package com.birol.ems.project.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
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
import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.dto.EmpTimeReportDTO;
import com.birol.ems.project.dao.ProjectDao;
import com.birol.ems.project.dao.Project_ActivityDao;
import com.birol.ems.project.dao.Project_ApplicantDao;
import com.birol.ems.project.dao.Project_WorkersDao;
import com.birol.ems.project.dto.Project;
import com.birol.ems.project.dto.Project_Activity;
import com.birol.ems.project.dto.Project_Applicant;
import com.birol.ems.project.dto.Project_Workers;
import com.birol.persistence.dao.RoleRepository;
import com.birol.persistence.model.User;

@Controller
public class ProjectController {
	
	@Autowired
	private ProjectDao projectDao;
	@Autowired
	private Project_WorkersDao project_WorkersDao;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	private Project_ApplicantDao project_ApplicantDao;
	@Autowired
	private Project_ActivityDao project_ActivityDao;
	@Autowired
	EmpTimeReportRepo avrepo;

	@GetMapping("/projects")
	public ModelAndView projectsHome(final ModelMap model, Authentication auth) {
		User user = (User) auth.getPrincipal();
		model.addAttribute("user", user);
		
		ArrayList<Project> allprojects =(ArrayList<Project>) projectDao.findAll();
		ArrayList<Project> myprojects= new ArrayList<Project>();
		
		for(Project x: allprojects) {
			if (x.getProject_image() != null) {
				String imageencode = Base64.getEncoder().encodeToString(x.getProject_image());
				x.setProject_image_encoded(imageencode);
			}
			if(x.getCreatorid()==user.getId()) {
				myprojects.add(x);
			}else {
				for(Project_Workers pw: x.getWorkers() )
				if(pw.getEmpid()==user.getId())myprojects.add(x);
			}
						
		}		
		model.addAttribute("my", myprojects);
		model.addAttribute("all", allprojects);		
		return new ModelAndView("ems/pages/project/projectHome", model);
	}
	
	
	@RequestMapping(value = "/createproject", method = RequestMethod.POST)
	public ModelAndView createproject(@ModelAttribute Project project, ModelMap model, Authentication auth,
			final HttpServletRequest request) {
		User user = (User) auth.getPrincipal();
		project.setCreatorid(user.getId());
		project.setCreatorname(user.getFirstName()+" "+user.getLastName());
		project.setStatus("New");

		try {
			if (project.getProject_image_m().getSize() > 0)
				project.setProject_image(project.getProject_image_m().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}	

		Project newProject=  projectDao.save(project);
		
		Project_Workers participants= new Project_Workers();
		participants.setId(String.valueOf(newProject.getProjectid())+String.valueOf(newProject.getCreatorid()));
		participants.setProjectid(newProject.getProjectid());
		participants.setEmpid(newProject.getCreatorid());
		participants.setEmp_name(newProject.getCreatorname());
		participants.setIsadmin(true);
		project_WorkersDao.save(participants);
		
		return new ModelAndView("redirect:/projects", model);
	}
	
	@RequestMapping(value = "/editproject", method = RequestMethod.POST)
	public ModelAndView editproject(@ModelAttribute Project project, ModelMap model, Authentication auth,
			final HttpServletRequest request) {
		User user = (User) auth.getPrincipal();
		
		Project existProject=  projectDao.findProjectByprojectid(project.getProjectid()).get();
		existProject.setCreated(existProject.getCreated());
		existProject.setTitle(project.getTitle());
		existProject.setProjectkey(project.getProjectkey());
		existProject.setDescription(project.getDescription());
		existProject.setIsprivate(project.isIsprivate());
		existProject.setStatus(project.getStatus());
		
		try {
			if (project.getProject_image_m().getSize() > 0) {
				existProject.setProject_image(project.getProject_image_m().getBytes());
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}	

		Project editProject=  projectDao.save(existProject);
		
		Project_Activity activity = new Project_Activity();
		activity.setMessage("Project Setting Updated");
		activity.setCreatorid(user.getId());
		activity.setCreatorname(user.getFirstName()+" "+user.getLastName());
		activity.setProjectid(editProject.getProjectid());
		activity.setType("SETTING");
		project_ActivityDao.save(activity);
		
		
		String redirurl= "viewProject?projectid="+editProject.getProjectid();
		return new ModelAndView("redirect:/"+redirurl, model);
	}
	
	@GetMapping("/viewProject")
	public ModelAndView viewProject(@RequestParam("projectid") Long projectid,Authentication auth, final ModelMap model) {
		Optional<Project> project= projectDao.findProjectByprojectid(projectid);
		User user = (User) auth.getPrincipal();		
		if (project.get().getProject_image() != null) {
			String imageencode = Base64.getEncoder().encodeToString(project.get().getProject_image());
			project.get().setProject_image_encoded(imageencode);
		}		
		Project_Workers meexists= null;
		Project_Applicant applicantexists= null;
		for(Project_Workers w: project.get().getWorkers()) {
			if(w.getEmpid()==user.getId())meexists=w;
			if(w.getEmp().getEmp_image()!=null) {
				String imageencode = Base64.getEncoder().encodeToString(w.getEmp().getEmp_image());
				w.getEmp().setEmp_image_encoded(imageencode);
			}
		}
		
		for(Project_Applicant a: project.get().getApplicants()) {
			if(a.getEmpid()==user.getId())applicantexists=a;
			if(a.getEmp().getEmp_image()!=null) {
				String imageencode = Base64.getEncoder().encodeToString(a.getEmp().getEmp_image());
				a.getEmp().setEmp_image_encoded(imageencode);
			}
		}
		
		
		model.addAttribute("thisprojectapplicant", applicantexists);
		model.addAttribute("project", project.get());
		if(meexists==null) return new ModelAndView("ems/pages/project/viewProjectUnauthorized", model);
		
		ArrayList<Project_Activity> activity= project_ActivityDao.findByProjectidOrderByCreatedDesc(projectid);
		
		model.addAttribute("thisprojectemp", meexists);
		model.addAttribute("activity", activity);
		
		if(!meexists.isIsadmin()) return new ModelAndView("ems/pages/project/viewProjectAsEmployee", model);
			
		model.addAttribute("roles", roleRepository.findAll());
		ArrayList<EmpTimeReportDTO> availist = avrepo.findbyProjectidAndApproved(projectid);		
		model.addAttribute("wsh", availist);
		return new ModelAndView("ems/pages/project/viewProjectAdmin", model);
		
	}
	
	@GetMapping("/applyToProject")
	public ModelAndView applyToProject(@RequestParam("projectid") Long projectid,Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();		
		Project_Applicant pa =new Project_Applicant();
		pa.setId("PA"+String.valueOf(projectid)+String.valueOf(user.getId()));
		pa.setEmpid(user.getId());
		pa.setEmp_name(user.getFirstName()+" "+user.getLastName());
		pa.setProjectid(projectid);		
		project_ApplicantDao.save(pa);
		
		Project_Activity activity = new Project_Activity();
		activity.setMessage("New Apllicant");
		activity.setCreatorid(user.getId());
		activity.setCreatorname(user.getFirstName()+" "+user.getLastName());
		activity.setProjectid(projectid);
		activity.setType("PARTICIPANT");
		project_ActivityDao.save(activity);
		
		
		String redirurl= "viewProject?projectid="+projectid;
		return new ModelAndView("redirect:/"+redirurl, model);
	}
	
	@GetMapping("/approveEmptoProject")
	@ResponseBody
	public String approveEmptoProject(@RequestParam(value="empid") int empid, Authentication auth,
			@RequestParam(value="projectid") long projectid,
			@RequestParam(value="approve") int approve){
		String msg="";
		Project_Activity activity = new Project_Activity();
		User user = (User) auth.getPrincipal();	
		
		try {
			Project_Applicant pa = project_ApplicantDao.findById("PA"+projectid+empid).get();
			if(approve==0) {
				project_ApplicantDao.delete(pa);
				String a="Application Declined for "+pa.getEmp_name();
				activity.setMessage(a);
				activity.setCreatorid(user.getId());
				activity.setCreatorname(user.getFirstName()+" "+user.getLastName());
				activity.setProjectid(projectid);
				activity.setType("PARTICIPANT");
				activity.setTargetuser(empid);
				project_ActivityDao.save(activity);
				return a;
			}
			
			Project_Workers pw = new Project_Workers();
			
			pw.setId(String.valueOf(projectid)+empid);
			pw.setEmpid(pa.getEmpid());
			pw.setEmp_name(pa.getEmp_name());
			pw.setProjectid(pa.getProjectid());
			
			if(approve==1) {
				String a= pw.getEmp_name()+ " is now a participant to this project";
				project_WorkersDao.save(pw);
				project_ApplicantDao.delete(pa);
				activity.setMessage(a);
				activity.setCreatorid(user.getId());
				activity.setCreatorname(user.getFirstName()+" "+user.getLastName());
				activity.setProjectid(projectid);
				activity.setType("PARTICIPANT");
				project_ActivityDao.save(activity);
				return a;				
				
			}
		} catch (Exception e) {
			msg= e.getMessage();
		}		
		return msg;
	}
	
	@GetMapping("/removeFromProject")
	public ModelAndView removeFromProject(@RequestParam("projectid") Long projectid,
			@RequestParam("workerid") String workerid,
			Authentication auth, final ModelMap model) {
		User user = (User) auth.getPrincipal();		
		
		project_WorkersDao.deleteById(workerid);		
		
		Project_Activity activity = new Project_Activity();
		activity.setMessage("Removed a Participant");
		activity.setCreatorid(user.getId());
		activity.setCreatorname(user.getFirstName()+" "+user.getLastName());
		activity.setProjectid(projectid);
		activity.setType("PARTICIPANT");
		activity.setTargetuser(Long.parseLong(workerid));
		project_ActivityDao.save(activity);
		
		
		String redirurl= "viewProject?projectid="+projectid;
		return new ModelAndView("redirect:/"+redirurl, model);
	}
	
	@GetMapping("/addParticipanttoProject")
	public ModelAndView addParticipanttoProject(
			@RequestParam("projectid") Long projectid,
			@RequestParam("emp") String emp,Authentication auth, final ModelMap model) {
		String redirurl= "viewProject?projectid="+projectid;
		User user = (User) auth.getPrincipal();		
		String[] empsplit= emp.split("#");
		String pwid=String.valueOf(projectid)+empsplit[1];
		try {
			Project_Workers pvw= project_WorkersDao.findById(pwid).get();
			if(pvw!=null)
				return new ModelAndView("redirect:/"+redirurl, model);			
		}catch (NoSuchElementException e) {}
		
		Project_Workers pw = new Project_Workers();		
		pw.setId(String.valueOf(projectid)+empsplit[1]);
		pw.setEmpid(Long.parseLong(empsplit[1]));
		pw.setEmp_name(empsplit[0].trim());
		pw.setProjectid(projectid);
		project_WorkersDao.save(pw);
		
		Project_Activity activity = new Project_Activity();
		activity.setMessage(empsplit[0]+ "is now a participant to this project");
		activity.setCreatorid(user.getId());
		activity.setCreatorname(user.getFirstName()+" "+user.getLastName());
		activity.setProjectid(projectid);
		activity.setType("PARTICIPANT");
		activity.setTargetuser(Long.parseLong(empsplit[1]));
		project_ActivityDao.save(activity);
		return new ModelAndView("redirect:/"+redirurl, model);
	}
	
	@GetMapping("/makeAdmin")
	@ResponseBody
	public String makeAdmin(@RequestParam(value="empid") int empid, Authentication auth,
			@RequestParam(value="projectid") long projectid){
		try {
			User user = (User) auth.getPrincipal();		
			Project_Workers pw =project_WorkersDao.findById(String.valueOf(projectid)+empid).get();
			pw.setIsadmin(true);
			project_WorkersDao.save(pw);
			
			Project_Activity activity = new Project_Activity();
			activity.setMessage(pw.getEmp_name()+ " is now an ADMIN to the project");
			activity.setCreatorid(user.getId());
			activity.setCreatorname(user.getFirstName()+" "+user.getLastName());
			activity.setProjectid(projectid);
			activity.setType("PARTICIPANT");
			project_ActivityDao.save(activity);
			
		}catch (Exception e) {
			return "Error!";
		}		
		return "ADMIN";
		
	}
	
	@GetMapping("/removeAdmin")
	@ResponseBody
	public String removeAdmin(@RequestParam(value="empid") int empid, Authentication auth,
			@RequestParam(value="projectid") long projectid){
		try {
			User user = (User) auth.getPrincipal();	
			ArrayList<Project_Workers> pwlist= project_WorkersDao.findbyprojectidAndisadmin(projectid);
			if(pwlist.size()<2)return "Last Admin";
			Project_Workers pw =project_WorkersDao.findById(String.valueOf(projectid)+empid).get();
			pw.setIsadmin(false);
			project_WorkersDao.save(pw);
			
			Project_Activity activity = new Project_Activity();
			activity.setMessage(pw.getEmp_name()+ " is no longer an admin to this project");
			activity.setCreatorid(user.getId());
			activity.setCreatorname(user.getFirstName()+" "+user.getLastName());
			activity.setProjectid(projectid);
			activity.setType("PARTICIPANT");
			activity.setTargetuser(pw.getEmpid());
			project_ActivityDao.save(activity);
			
		}catch (Exception e) {
			return "Error!";
		}		
		return "EMPLOYEE";
		
	}
}
