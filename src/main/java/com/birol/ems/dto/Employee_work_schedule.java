package com.birol.ems.dto;

import java.sql.Time;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
@Entity
public class Employee_work_schedule {
	@Id
	private String  work_sh_id; 
	private String userid; 	
	private String full_name; 
	private String from_date; 	
	private String to_date 	;
	private String lunch_hour; 	
	private String work_start ;	
	private String work_end ;	
	private String status ;
	private String assigned_by;
	private String assigned_by_full_name;	
	private String work_desc;	
	private String week ;		
	private String day 	;
	private String date ;		
	private String work_hour ;		
	
	
	public String getWork_hour() {
		return work_hour;
	}
	public void setWork_hour(String work_hour) {
		this.work_hour = work_hour;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getWork_desc() {
		return work_desc;
	}
	public void setWork_desc(String work_desc) {
		this.work_desc = work_desc;
	}
	public String getAssigned_by() {
		return assigned_by;
	}
	public void setAssigned_by(String assigned_by) {
		this.assigned_by = assigned_by;
	}
	public String getAssigned_by_full_name() {
		return assigned_by_full_name;
	}
	public void setAssigned_by_full_name(String assigned_by_full_name) {
		this.assigned_by_full_name = assigned_by_full_name;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
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
	public String getLunch_hour() {
		return lunch_hour;
	}
	public void setLunch_hour(String lunch_hour) {
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
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getWork_sh_id() {
		return work_sh_id;
	}
	public void setWork_sh_id(String work_sh_id) {
		this.work_sh_id = work_sh_id;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	@Override
	public String toString() {
		return "Employee_work_schedule [work_sh_id=" + work_sh_id + ", userid=" + userid + ", full_name=" + full_name
				+ ", from_date=" + from_date + ", to_date=" + to_date + ", lunch_hour=" + lunch_hour + ", work_start="
				+ work_start + ", work_end=" + work_end + ", status=" + status
				+ ", assigned_by=" + assigned_by + ", assigned_by_full_name=" + assigned_by_full_name + ", work_desc="
				+ work_desc + ", week=" + week + ", day=" + day + ", date=" + date + ", work_hour=" + work_hour + "]";
	}
	
		
	

}