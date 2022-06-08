package com.birol.ems.project.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.birol.ems.project.dto.Project_Task;

public interface Project_TaskDao extends JpaRepository<Project_Task, Integer> {

	Project_Task findBytaskid(Long taskid);
	
	
}
