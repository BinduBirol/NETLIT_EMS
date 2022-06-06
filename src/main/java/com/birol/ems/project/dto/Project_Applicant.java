package com.birol.ems.project.dto;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.birol.ems.dto.EMPLOYEE_BASIC;

@Entity
public class Project_Applicant {
	@Id
	private String id;
	private long projectid;
	private long empid;
	private String emp_name;
	@OneToOne
	@JoinColumn(name = "empid",insertable=false ,updatable=false)
	private EMPLOYEE_BASIC emp;
	
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

	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	public long getProjectid() {
		return projectid;
	}

	public void setProjectid(long projectid) {
		this.projectid = projectid;
	}

	public long getEmpid() {
		return empid;
	}

	public void setEmpid(long empid) {
		this.empid = empid;
	}

	public String getEmp_name() {
		return emp_name;
	}

	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
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

	public EMPLOYEE_BASIC getEmp() {
		return emp;
	}

	public void setEmp(EMPLOYEE_BASIC emp) {
		this.emp = emp;
	}
	
	
	

}
