package com.birol.ems.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
import org.springframework.web.servlet.ModelAndView;

import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.project.dao.ProjectDao;
import com.birol.ems.project.dao.Project_WorkersDao;
import com.birol.ems.project.dto.Project;
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

	@GetMapping("/projects")
	public ModelAndView profile(final ModelMap model, Authentication auth) {
		User user = (User) auth.getPrincipal();
		model.addAttribute("user", user);
		
		ArrayList<Project> allprojects =(ArrayList<Project>) projectDao.findAll();
		ArrayList<Project> myprojects= new ArrayList<Project>();
		for(Project x: allprojects) {
			if (x.getProject_image() != null) {
				String imageencode = Base64.getEncoder().encodeToString(x.getProject_image());
				x.setProject_image_encoded(imageencode);
			}
			if(x.getCreatorid()==user.getId())myprojects.add(x);
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
		
		return new ModelAndView("redirect:/projects", model);
	}
	
	@GetMapping("/viewProjectAdmin")
	public ModelAndView viewProjectAdmin(@RequestParam("projectid") Long projectid,Authentication auth, final ModelMap model) {
		Optional<Project> project= projectDao.findProjectByprojectid(projectid);
		boolean adminauth= false;
		User user = (User) auth.getPrincipal();		
		if (project.get().getProject_image() != null) {
			String imageencode = Base64.getEncoder().encodeToString(project.get().getProject_image());
			project.get().setProject_image_encoded(imageencode);
		}
		for(Project_Workers w: project.get().getWorkers()) {
			
			if(w.getEmpid()==user.getId()&& w.isIsadmin()==true) adminauth=true;
			
			if(w.getEmp().getEmp_image()!=null) {
				String imageencode = Base64.getEncoder().encodeToString(w.getEmp().getEmp_image());
				w.getEmp().setEmp_image_encoded(imageencode);
			}
		}		
		model.addAttribute("project", project.get());		
		if(!adminauth) return new ModelAndView("ems/pages/project/viewProjectAdminUnauthorized", model);
			
		model.addAttribute("roles", roleRepository.findAll());
		return new ModelAndView("ems/pages/project/viewProjectAdmin", model);
		
	}
}
