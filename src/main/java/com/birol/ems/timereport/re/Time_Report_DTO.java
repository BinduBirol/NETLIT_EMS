package com.birol.ems.timereport.re;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name="time_report")
public class Time_Report_DTO {
	
	@Id
	private String tr_id;
	private long empid;
	private String full_name;	
	private String work_start;
	private String work_end;
	private int work_interval;
	@Column(nullable=false)
	private int status;
	
	@JoinColumn(name="status", referencedColumnName="typeid",insertable=false, updatable=false)
	@OneToOne
	@LazyCollection(LazyCollectionOption.FALSE)	
	private TimeReportTypesDTO trTypes;
	
	private String work_desc;
	private int week;
	private String day;
	private LocalDate date;
	private long work_minute;
	private String work_hour;
	private boolean isapproved;
	private boolean isrejected;
	private Date created;
	private Date updated;
	
	@Transient
	private String bg_class;	
	@Transient
	private String from_date;	
	@Transient
	private String to_date;	
	

	@PrePersist
	protected void onCreate() {
		created = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		updated = new Date();
	}

	public String getTr_id() {
		return tr_id;
	}

	public void setTr_id(String tr_id) {
		this.tr_id = tr_id;
	}

	public long getEmpid() {
		return empid;
	}

	public void setEmpid(long empid) {
		this.empid = empid;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
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
	

	

	public int getWork_interval() {
		return work_interval;
	}

	public void setWork_interval(int work_interval) {
		this.work_interval = work_interval;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public TimeReportTypesDTO getTrTypes() {
		return trTypes;
	}

	public void setTrTypes(TimeReportTypesDTO trTypes) {
		this.trTypes = trTypes;
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

	public Time_Report_DTO() {
		super();
		// TODO Auto-generated constructor stub
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
	
	

}
