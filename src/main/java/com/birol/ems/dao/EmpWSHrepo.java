package com.birol.ems.dao;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.birol.ems.dto.Employee_work_schedule;

public interface EmpWSHrepo extends JpaRepository<Employee_work_schedule, String> {
	ArrayList<Employee_work_schedule> findByUserid(long userid);
}
