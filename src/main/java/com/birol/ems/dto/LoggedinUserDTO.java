package com.birol.ems.dto;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tracklogin")
public class LoggedinUserDTO implements Comparable<LoggedinUserDTO>{
	@Id
	public long id;
	public String email;
	public String fullname;
	public Date loggedin;
	public Date loggedout;
	public boolean isloggedin;
	public String image_encoded;

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

	public String getImage_encoded() {
		return image_encoded;
	}

	public void setImage_encoded(String image_encoded) {
		this.image_encoded = image_encoded;
	}
	
	
	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	@Override
	public String toString() {
		return "LoggedinUserDTO [id=" + id + ", email=" + email + ", loggedin=" + loggedin + ", loggedout=" + loggedout
				+ ", isloggedin=" + isloggedin + ", image_encoded=" + image_encoded + ", getId()=" + getId()
				+ ", isIsloggedin()=" + isIsloggedin() + ", getLoggedin()=" + getLoggedin() + ", getLoggedout()="
				+ getLoggedout() + ", getEmail()=" + getEmail() + ", getImage_encoded()=" + getImage_encoded()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
	
	@Override
    public int compareTo(LoggedinUserDTO e) {
        return getLoggedin().compareTo(e.getLoggedin());
    }

	
}
