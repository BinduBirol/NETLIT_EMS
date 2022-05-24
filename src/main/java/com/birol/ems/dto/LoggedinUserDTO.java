package com.birol.ems.dto;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tracklogin")
public class LoggedinUserDTO {
	@Id
	public String email;
	public Date loggedin;
	public Date loggedout;	
	
	public Date getLoggedin() {
		return loggedin;
	}
	public void setLoggedin(Date loggedin) {
		this.loggedin = loggedin;
	}
	public Date getLoggedout() {
		return loggedout;
	}
	public void setLoggedout(Date loggedout) {
		this.loggedout = loggedout;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public LoggedinUserDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
