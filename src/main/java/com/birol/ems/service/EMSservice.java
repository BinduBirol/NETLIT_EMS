package com.birol.ems.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.birol.ems.dao.LoggedinUserRepo;
import com.birol.ems.dto.LoggedinUserDTO;
import com.birol.security.ActiveUserStore;
import com.birol.security.LoggedUser;

@Service
public class EMSservice {
	@Autowired
	public LoggedinUserRepo loggedinUserRepo;
	@Autowired
	ActiveUserStore activeUserStore;

	public ArrayList<LoggedinUserDTO> getloggedinusers(){
		ArrayList<LoggedinUserDTO> users = (ArrayList<LoggedinUserDTO>) activeUserStore.getLoggedusers();
		return users;
	}
	
	public void saveloggedininfo(LoggedUser user) {
		Date date = new Date();
		LoggedinUserDTO l = new LoggedinUserDTO();
		l.setEmail(user.getUsername());
		l.setLoggedin(date);
		l.setLoggedout(null);
		System.out.println(date);
		//loggedinUserRepo.save(l);
	}

	public void saveloggedoutinfo(LoggedUser user) {
		Date date = new Date();
		LoggedinUserDTO l = new LoggedinUserDTO();
		l.setEmail(user.getUsername());
		l.setLoggedin(null);
		l.setLoggedout(date);
		loggedinUserRepo.save(l);
	}
	
	public List<LoggedinUserDTO> getLoggedinInfo() {		
		return loggedinUserRepo.findAll();
	}
}
