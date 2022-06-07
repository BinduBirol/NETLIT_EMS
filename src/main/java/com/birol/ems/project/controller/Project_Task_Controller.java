package com.birol.ems.project.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.birol.ems.project.dao.Project_TaskDao;
import com.birol.ems.project.dto.Project;
import com.birol.ems.project.dto.Project_Task;
import com.birol.persistence.model.User;

@Controller
public class Project_Task_Controller {
	
	@Autowired
	private Project_TaskDao project_TaskDao;
	
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String redirurl= "viewProject?projectid="+pt.getProjectid();
		return new ModelAndView("redirect:/"+redirurl, model);
	}

}
