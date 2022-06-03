package com.birol.ems.project.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.web.multipart.MultipartFile;


@Entity
public class Project {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long projectid;
	private String title;
	private String description;
	
	@OneToMany(mappedBy = "projectid")
	@LazyCollection(LazyCollectionOption.FALSE)	
	private List<Project_Applicant> applicants;
	private String projectkey;
	
	@OneToMany(mappedBy = "projectid")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Project_Workers> workers;
	private String status;
	private boolean isprivate;
	
	@Lob
	private byte[] project_image;
	@Transient
	private MultipartFile project_image_m;
	@Transient
	private String project_image_encoded;
	
	private long creatorid;
	private String creatorname;
	
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

	
	public long getProjectid() {
		return projectid;
	}

	public void setProjectid(long projectid) {
		this.projectid = projectid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

	
	

	public String getProjectkey() {
		return projectkey;
	}

	public void setProjectkey(String projectkey) {
		this.projectkey = projectkey;
	}

	public List<Project_Applicant> getApplicants() {
		return applicants;
	}

	public void setApplicants(List<Project_Applicant> applicants) {
		this.applicants = applicants;
	}

	public List<Project_Workers> getWorkers() {
		return workers;
	}

	public void setWorkers(List<Project_Workers> workers) {
		this.workers = workers;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public byte[] getProject_image() {
		return project_image;
	}

	public void setProject_image(byte[] project_image) {
		this.project_image = project_image;
	}

	public MultipartFile getProject_image_m() {
		return project_image_m;
	}

	public void setProject_image_m(MultipartFile project_image_m) {
		this.project_image_m = project_image_m;
	}

	public String getProject_image_encoded() {
		return project_image_encoded;
	}

	public void setProject_image_encoded(String project_image_encoded) {
		this.project_image_encoded = project_image_encoded;
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

	public boolean isIsprivate() {
		return isprivate;
	}

	public void setIsprivate(boolean isprivate) {
		this.isprivate = isprivate;
	}

	public long getCreatorid() {
		return creatorid;
	}

	public void setCreatorid(long creatorid) {
		this.creatorid = creatorid;
	}

	public String getCreatorname() {
		return creatorname;
	}

	public void setCreatorname(String creatorname) {
		this.creatorname = creatorname;
	}

	
	
}
