package com.birol.ems.dao;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.birol.ems.dto.Employee_work_schedule;

public interface EmpWSHrepo extends JpaRepository<Employee_work_schedule, String> {
	String getwhbyUser= "SELECT * FROM employee_work_schedule WHERE userId = ?1  order by date desc";
	@Query(value =getwhbyUser,nativeQuery = true)
	ArrayList<Employee_work_schedule> findByUserid(long userid);
	
	String getuserwshbydate= "SELECT * FROM employee_work_schedule WHERE userId = ?1  and date=?2 ";
	@Query(value =getuserwshbydate,nativeQuery = true)
	Employee_work_schedule findByUserIdAndDate(long userid, Date d);
	
	String getuserwshbydaterange= "SELECT * FROM employee_work_schedule WHERE userId = ?1  and date between ?2 and ?3 ";
	@Query(value =getuserwshbydaterange,nativeQuery = true)
	ArrayList<Employee_work_schedule> findByUserIdAndDateRange(long userid, Date fd, Date td);
	
	@Query(value = "SELECT * from employee_work_schedule  where userId = :userid AND  date BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d')  AND STR_TO_DATE(:endDate, '%Y-%m-%d')",nativeQuery = true)
	public ArrayList<Employee_work_schedule> getAllBetweenDates(@Param("userid")long userid, @Param("startDate")String startDate,@Param("endDate")String endDate);
	
	String deleteByAvid= "DELETE FROM employee_work_schedule WHERE availability_id = ?1 ";
	@Query(value =deleteByAvid,nativeQuery = true)
	String deleteByAvid(String availability_id);
}
