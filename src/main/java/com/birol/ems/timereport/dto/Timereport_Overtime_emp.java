package com.birol.ems.timereport.dto;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import org.springframework.lang.NonNull;

@Entity
public class Timereport_Overtime_emp {
	@Id
	private String ob_id;	
	@NonNull
	private String av_id;
	@NonNull
	private long userid;
	@NonNull
	private String full_name;
	private String from_date;
	private String to_date;
	private int lunch_hour;
	private String work_start;
	private String work_end;
	private int status;
	private int obtype;
	private int obno;
	private String work_desc;
	private int week;
	private String day;
	@NonNull
	private LocalDate date;
	private long work_minute;
	private String work_hour;
	@Transient
	private String bg_class;
	
	private boolean isapproved;
	private boolean isrejected;
	
	private long taskid;
	private long projectid;

	private Date created;
	private Date updated;
	
	

	

	public Timereport_Overtime_emp (long userid, LocalDate date) {
		super();
		this.userid = userid;
		this.date = date;
	}

	@PrePersist
	protected void onCreate() {
		created = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		updated = new Date();
	}

	public String getOb_id() {
		return ob_id;
	}

	public void setOb_id(String ob_id) {
		this.ob_id = ob_id;
	}

	public String getAv_id() {
		return av_id;
	}

	public void setAv_id(String av_id) {
		this.av_id = av_id;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getFrom_date() {
		return from_date;
	}

	public void setFrom_date(String from_date) {
		this.from_date = from_date;
	}

	public String getTo_date() {
		return to_date;
	}

	public void setTo_date(String to_date) {
		this.to_date = to_date;
	}

	public int getLunch_hour() {
		return lunch_hour;
	}

	public void setLunch_hour(int lunch_hour) {
		this.lunch_hour = lunch_hour;
	}

	public String getWork_start() {
		return work_start;
	}

	public void setWork_start(String work_start) {
		this.work_start = work_start;
	}

	public String getWork_end() {
		return work_end;
	}

	public void setWork_end(String work_end) {
		this.work_end = work_end;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getObtype() {
		return obtype;
	}

	public void setObtype(int obtype) {
		this.obtype = obtype;
	}

	

	public int getObno() {
		return obno;
	}

	public void setObno(int obno) {
		this.obno = obno;
	}

	public String getWork_desc() {
		return work_desc;
	}

	public void setWork_desc(String work_desc) {
		this.work_desc = work_desc;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public long getWork_minute() {
		return work_minute;
	}

	public void setWork_minute(long work_minute) {
		this.work_minute = work_minute;
	}

	public String getWork_hour() {
		return work_hour;
	}

	public void setWork_hour(String work_hour) {
		this.work_hour = work_hour;
	}

	public String getBg_class() {
		return bg_class;
	}

	public void setBg_class(String bg_class) {
		this.bg_class = bg_class;
	}

	public boolean isIsapproved() {
		return isapproved;
	}

	public void setIsapproved(boolean isapproved) {
		this.isapproved = isapproved;
	}

	public boolean isIsrejected() {
		return isrejected;
	}

	public void setIsrejected(boolean isrejected) {
		this.isrejected = isrejected;
	}

	public long getTaskid() {
		return taskid;
	}

	public void setTaskid(long taskid) {
		this.taskid = taskid;
	}

	public long getProjectid() {
		return projectid;
	}

	public void setProjectid(long projectid) {
		this.projectid = projectid;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Timereport_Overtime_emp() {
		super();
		// TODO Auto-generated constructor stub
	}	

	
}
