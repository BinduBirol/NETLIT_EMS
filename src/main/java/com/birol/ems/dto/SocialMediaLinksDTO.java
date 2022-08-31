package com.birol.ems.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EMP_SOCIALMEDIA_LINKS")
public class SocialMediaLinksDTO {

	@Id
	public long empid;
	@Column(nullable = true)
	public String facebook;
	@Column(nullable = true)
	public String instagram;
	@Column(nullable = true)
	public String linkedin;
	@Column(nullable = true)
	public String twitter;
	
	public long getEmpid() {
		return empid;
	}
	public void setEmpid(long empid) {
		this.empid = empid;
	}
	public String getFacebook() {
		return facebook;
	}
	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}
	public String getInstagram() {
		return instagram;
	}
	public void setInstagram(String instagram) {
		this.instagram = instagram;
	}
	public String getLinkedin() {
		return linkedin;
	}
	public void setLinkedin(String linkedin) {
		this.linkedin = linkedin;
	}
	public String getTwitter() {
		return twitter;
	}
	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}
	public SocialMediaLinksDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public SocialMediaLinksDTO(long empid, String facebook, String instagram, String linkedin, String twitter) {
		super();
		this.empid = empid;
		this.facebook = facebook;
		this.instagram = instagram;
		this.linkedin = linkedin;
		this.twitter = twitter;
	}
	
	public SocialMediaLinksDTO(long empid) {
		super();
		this.empid = empid;
	}
	
	
	
}
