package com.birol.ems.project.dao;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.birol.ems.project.dto.Project_Activity;

public interface Project_ActivityDao extends JpaRepository<Project_Activity, Integer>{

	ArrayList<Project_Activity> findByProjectidOrderByCreatedDesc(Long projectid);

}
