package com.birol.ems.contract;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "contract_signer_info")
public class Signer_DTO {
	@Id	
	@GeneratedValue(strategy=GenerationType.AUTO)
	public long signer_id;
	public long contractid;
	public long signer_empid;
	
	public String signer_name;
	public String signer_email;
	public String signer_phone;
	public String signer_company;
	public boolean issigned;
	
	public long getSigner_id() {
		return signer_id;
	}
	public void setSigner_id(long signer_id) {
		this.signer_id = signer_id;
	}
	public long getContractid() {
		return contractid;
	}
	public void setContractid(long contractid) {
		this.contractid = contractid;
	}
	public long getSigner_empid() {
		return signer_empid;
	}
	public void setSigner_empid(long signer_empid) {
		this.signer_empid = signer_empid;
	}
	public String getSigner_name() {
		return signer_name;
	}
	public void setSigner_name(String signer_name) {
		this.signer_name = signer_name;
	}
	public String getSigner_email() {
		return signer_email;
	}
	public void setSigner_email(String signer_email) {
		this.signer_email = signer_email;
	}
	public String getSigner_phone() {
		return signer_phone;
	}
	public void setSigner_phone(String signer_phone) {
		this.signer_phone = signer_phone;
	}
	public String getSigner_company() {
		return signer_company;
	}
	public void setSigner_company(String signer_company) {
		this.signer_company = signer_company;
	}
	public boolean isIssigned() {
		return issigned;
	}
	public void setIssigned(boolean issigned) {
		this.issigned = issigned;
	}
	
	
}
