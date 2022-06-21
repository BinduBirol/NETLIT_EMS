package com.birol.ems.timereport.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Overtime_Type {

	@Id
	public int typeid; 
	public String typename; 
	public int percentage; 
	public boolean isactive;
	
	public String start; 
	public String end; 
	public int interval_minutes;
	
	public Overtime_Type() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Overtime_Type(int typeid, String typename, int percentage, boolean isactive) {
		super();
		this.typeid = typeid;
		this.typename = typename;
		this.percentage = percentage;
		this.isactive = isactive;
	}
	public int getTypeid() {
		return typeid;
	}
	public void setTypeid(int typeid) {
		this.typeid = typeid;
	}
	public String getTypename() {
		return typename;
	}
	public void setTypename(String typename) {
		this.typename = typename;
	}
	public int getPercentage() {
		return percentage;
	}
	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}
	public boolean isIsactive() {
		return isactive;
	}
	public void setIsactive(boolean isactive) {
		this.isactive = isactive;
	}
	
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public Overtime_Type(int typeid, String typename, int percentage, String start, String end) {
		super();
		this.typeid = typeid;
		this.typename = typename;
		this.percentage = percentage;
		this.start = start;
		this.end = end;
	}
	public int getInterval_minutes() {
		return interval_minutes;
	}
	public void setInterval_minutes(int interval_minutes) {
		this.interval_minutes = interval_minutes;
	}
	public Overtime_Type(int typeid, String typename, int percentage, boolean isactive, String start, String end,
			int interval_minutes) {
		super();
		this.typeid = typeid;
		this.typename = typename;
		this.percentage = percentage;
		this.isactive = isactive;
		this.start = start;
		this.end = end;
		this.interval_minutes = interval_minutes;
	}
	
	
	
}
