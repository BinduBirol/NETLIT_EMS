package com.birol.ems.contract;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "contract_info")
public class Signer_DTO {
	@Id	
	@GeneratedValue(strategy=GenerationType.AUTO)
	public long id;
	public long contractid;
	public long empid;
	
	public String email;
	public String phone;
	public String company;
	public boolean issigned;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getContractid() {
		return contractid;
	}
	public void setContractid(long contractid) {
		this.contractid = contractid;
	}
	public long getEmpid() {
		return empid;
	}
	public void setEmpid(long empid) {
		this.empid = empid;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public boolean isIssigned() {
		return issigned;
	}
	public void setIssigned(boolean issigned) {
		this.issigned = issigned;
	}
}
