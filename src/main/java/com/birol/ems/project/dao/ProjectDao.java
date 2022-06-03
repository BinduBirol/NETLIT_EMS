package com.birol.ems.project.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.birol.ems.project.dto.Project;

public interface ProjectDao extends JpaRepository<Project, String> {

	Optional<Project> findProjectByprojectid(Long projectid);

	
}
