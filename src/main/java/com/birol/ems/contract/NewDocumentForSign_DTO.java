package com.birol.ems.contract;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.web.multipart.MultipartFile;
@Entity
@Table(name = "contract_info")
public class NewDocumentForSign_DTO {
	@Id	
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private String doc_name;
	private String category;
	@Lob
	private byte [] document_file;
	private LocalDate valid_till;
	@Lob 
	@Column(length=1000)
	private String email_message;
	private String invite_as;
	
	private long creator_id;
	private String creator_name;
	
	@OneToMany(mappedBy="contractid")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Signer_DTO> signers;
	@Transient
	private List<String> signers_str;
	@Transient
	private String valid_till_str;
	@Transient
	private MultipartFile document_file_m;
	
	@Column(nullable = false)
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDoc_name() {
		return doc_name;
	}

	public void setDoc_name(String doc_name) {
		this.doc_name = doc_name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public byte[] getDocument_file() {
		return document_file;
	}

	public void setDocument_file(byte[] document_file) {
		this.document_file = document_file;
	}

	public LocalDate getValid_till() {
		return valid_till;
	}

	public void setValid_till(LocalDate valid_till) {
		this.valid_till = valid_till;
	}

	public String getEmail_message() {
		return email_message;
	}

	public void setEmail_message(String email_message) {
		this.email_message = email_message;
	}

	public String getInvite_as() {
		return invite_as;
	}

	public void setInvite_as(String invite_as) {
		this.invite_as = invite_as;
	}

	public long getCreator_id() {
		return creator_id;
	}

	public void setCreator_id(long creator_id) {
		this.creator_id = creator_id;
	}

	public String getCreator_name() {
		return creator_name;
	}

	public void setCreator_name(String creator_name) {
		this.creator_name = creator_name;
	}

	public List<Signer_DTO> getSigners() {
		return signers;
	}

	public void setSigners(List<Signer_DTO> signers) {
		this.signers = signers;
	}

	public List<String> getSigners_str() {
		return signers_str;
	}

	public void setSigners_str(List<String> signers_str) {
		this.signers_str = signers_str;
	}

	public String getValid_till_str() {
		return valid_till_str;
	}

	public void setValid_till_str(String valid_till_str) {
		this.valid_till_str = valid_till_str;
	}

	public MultipartFile getDocument_file_m() {
		return document_file_m;
	}

	public void setDocument_file_m(MultipartFile document_file_m) {
		this.document_file_m = document_file_m;
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
	
}
