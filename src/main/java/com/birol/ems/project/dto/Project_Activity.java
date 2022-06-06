package com.birol.ems.project.dto;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import com.birol.persistence.model.User;

@Entity
public class Project_Activity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public long activityid;
	public String message;
	public long projectid;
	@Transient
	public String timediff;
	public String type;
	public long creatorid;
	public String creatorname;

	private Date created;

	@PrePersist
	protected void onCreate() {
		created = new Date();
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	

	public long getProjectid() {
		return projectid;
	}


	public void setProjectid(long projectid) {
		this.projectid = projectid;
	}


	public String getTimediff() {
		return timediff;
	}

	public void setTimediff(String timediff) {
		this.timediff = timediff;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getCreatorid() {
		return creatorid;
	}

	public void setCreatorid(long creatorid) {
		this.creatorid = creatorid;
	}

	

	public long getActivityid() {
		return activityid;
	}


	public void setActivityid(long activityid) {
		this.activityid = activityid;
	}


	public String getCreatorname() {
		return creatorname;
	}


	public void setCreatorname(String creatorname) {
		this.creatorname = creatorname;
	}


	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

}
