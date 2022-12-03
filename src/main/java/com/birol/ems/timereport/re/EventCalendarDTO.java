package com.birol.ems.timereport.re;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventCalendarDTO {
	
	private String title;
	private String start;
	private String end;
	private String color;
	private boolean allDay;
	
	private String trid;
	private String startHour;
	private String endHour;
	private int breakMin;
	private int typeid;
	private long work_minute;
	private String work_hour;
	private boolean isapproved;
	private boolean isrejected;
	private String workdesc;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public boolean isAllDay() {
		return allDay;
	}
	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}
	public String getTrid() {
		return trid;
	}
	public void setTrid(String trid) {
		this.trid = trid;
	}
	public String getStartHour() {
		return startHour;
	}
	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}
	public String getEndHour() {
		return endHour;
	}
	public void setEndHour(String endHour) {
		this.endHour = endHour;
	}
	public int getBreakMin() {
		return breakMin;
	}
	public void setBreakMin(int breakMin) {
		this.breakMin = breakMin;
	}
	public int getTypeid() {
		return typeid;
	}
	public void setTypeid(int typeid) {
		this.typeid = typeid;
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
	public String getWorkdesc() {
		return workdesc;
	}
	public void setWorkdesc(String workdesc) {
		this.workdesc = workdesc;
	}
	
	

}
