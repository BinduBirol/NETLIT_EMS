package com.birol.ems.dto;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

@Entity
public class Availability {
	@Id
	private String av_id;
	private long userid;
	private String full_name;
	private String from_date;
	private String to_date;
	private int lunch_hour;
	private String work_start;
	private String work_end;
	private int status;
	private String work_desc;
	private int week;
	private String day;
	private LocalDate date;
	private long work_minute;
	@Transient
	private String work_hour;
	
	private boolean isapproved;

	private Date created;
	private Date updated;

	@PrePersist
	protected void onCreate() {
		created = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		updated = new Date();
	}	

	public String getFull_name() {
		return full_name;
	}

	
	public boolean isIsapproved() {
		return isapproved;
	}

	public void setIsapproved(boolean isapproved) {
		this.isapproved = isapproved;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
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

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	
	public String getAv_id() {
		return av_id;
	}

	public void setAv_id(String av_id) {
		this.av_id = av_id;
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

	public long getWork_minute() {
		return work_minute;
	}

	public void setWork_minute(long work_minute) {
		this.work_minute = work_minute;
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
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

	public String getWork_hour() {
		return work_hour;
	}

	public void setWork_hour(String work_hour) {
		this.work_hour = work_hour;
	}

}