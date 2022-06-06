package com.birol.ems.project.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.birol.ems.project.dto.Project_Applicant;

public interface Project_ApplicantDao extends JpaRepository<Project_Applicant, String> {
	
}
