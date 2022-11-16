package com.birol.ems.timereport.re;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="TIME_REPORT_TYPES")
public class TimeReportTypesDTO {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int typeid; 
	public String typename;
	public String start; 
	public String end;
	public int interval_minutes;
	public int percentage; 
	public boolean isWorking;
	public boolean isOB;
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
	public int getInterval_minutes() {
		return interval_minutes;
	}
	public void setInterval_minutes(int interval_minutes) {
		this.interval_minutes = interval_minutes;
	}
	public int getPercentage() {
		return percentage;
	}
	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}
	public boolean isWorking() {
		return isWorking;
	}
	public void setWorking(boolean isWorking) {
		this.isWorking = isWorking;
	}
	public boolean isOB() {
		return isOB;
	}
	public void setOB(boolean isOB) {
		this.isOB = isOB;
	}
	public TimeReportTypesDTO(String typename, String start, String end, int interval_minutes,
			int percentage, boolean isWorking, boolean isOB) {
		super();
		this.typename = typename;
		this.start = start;
		this.end = end;
		this.interval_minutes = interval_minutes;
		this.percentage = percentage;
		this.isWorking = isWorking;
		this.isOB = isOB;
	}
	public TimeReportTypesDTO() {
		super();
		// TODO Auto-generated constructor stub
	}	
	
	
}
