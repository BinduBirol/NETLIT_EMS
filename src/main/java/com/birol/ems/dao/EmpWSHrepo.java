package com.birol.ems.dao;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.birol.ems.dto.Employee_work_schedule;

public interface EmpWSHrepo extends JpaRepository<Employee_work_schedule, String> {
	ArrayList<Employee_work_schedule> findByUseridOrderByDateAsc(long userid);
	String getuserwshbydate= "SELECT * FROM employee_work_schedule WHERE userId = ?1  and date=?2 ";
	@Query(value =getuserwshbydate,nativeQuery = true)
	Employee_work_schedule findByUserIdAndDate(long userid, Date d);
}
