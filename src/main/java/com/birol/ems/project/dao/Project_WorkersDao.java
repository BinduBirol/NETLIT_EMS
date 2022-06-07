package com.birol.ems.project.dao;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.birol.ems.project.dto.Project_Workers;

public interface Project_WorkersDao extends JpaRepository<Project_Workers, String>{

	String findbyprojectidAndisadminQ= "SELECT * FROM Project_Workers WHERE projectid = ?1  and isadmin=true ";
	@Query(value =findbyprojectidAndisadminQ,nativeQuery = true)
	ArrayList<Project_Workers> findbyprojectidAndisadmin(long projectid);

}
