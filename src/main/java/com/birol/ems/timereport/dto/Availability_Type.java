package com.birol.ems.timereport.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Availability_Type {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int id; 
	public int typeid; 
	public String typename; 
	public int percentage; 
	public boolean isactive;
	
	
	
	public Availability_Type() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Availability_Type(int id, int typeid, String typename, int percentage, boolean isactive) {
		super();
		this.id = id;
		this.typeid = typeid;
		this.typename = typename;
		this.percentage = percentage;
		this.isactive = isactive;
	}
	public Availability_Type(int typeid, String typename, int percentage, boolean isactive) {
		super();
		this.typeid = typeid;
		this.typename = typename;
		this.percentage = percentage;
		this.isactive = isactive;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
}
