package com.birol.security;

import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.birol.ems.dto.LoggedinUserDTO;
import com.birol.ems.service.EMSservice;

@Component
public class LoggedUser implements HttpSessionBindingListener {

	private String username;
	private Date time;
	private ActiveUserStore activeUserStore;
	@Autowired
	private EMSservice eMSservice;

	public LoggedUser(String username, ActiveUserStore activeUserStore) {
		this.username = username;
		this.activeUserStore = activeUserStore;
	}

	public LoggedUser() {
	}

	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		List<String> users = activeUserStore.getUsers();
		LoggedUser user = (LoggedUser) event.getValue();
		if (!users.contains(user.getUsername())) {
			users.add(user.getUsername());
		}

		List<LoggedinUserDTO> loggedusers = activeUserStore.getLoggedusers();
		boolean v = false;
		for (LoggedinUserDTO u : loggedusers) {
			if (u.getEmail().contains(user.getUsername())) {
				u.setIsloggedin(true);
				;
				v = true;
			}
		}
		if (!v) {
			Date now = new Date();
			LoggedinUserDTO l = new LoggedinUserDTO();
			l.setEmail(user.getUsername());
			l.setLoggedin(now);
			l.setIsloggedin(true);
			loggedusers.add(l);
		}
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		List<String> users = activeUserStore.getUsers();
		LoggedUser user = (LoggedUser) event.getValue();
		users.remove(user.getUsername());
		List<LoggedinUserDTO> loggedusers = activeUserStore.getLoggedusers();
		for (LoggedinUserDTO u : loggedusers) {
			if (u.getEmail().contains(user.getUsername())) {
				u.setIsloggedin(false);
			}
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}
