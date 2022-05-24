package com.birol.ems.dto;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tracklogin")
public class LoggedinUserDTO {
	@Id
	public long id;
	public String email;
	public Date loggedin;
	public Date loggedout;
	public boolean isloggedin;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isIsloggedin() {
		return isloggedin;
	}

	public void setIsloggedin(boolean isloggedin) {
		this.isloggedin = isloggedin;
	}

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
