package com.birol.ems.dao;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.birol.ems.dto.Availability;
import com.birol.ems.dto.Employee_work_schedule;

public interface AvailablityRepo extends JpaRepository<Availability, String> {

	@Query(value = "SELECT * from availability  where userId = :userid AND  date >= CURDATE() ",nativeQuery = true)
	ArrayList<Availability> getAvailablityByuseridandDategraterthanToday(@Param("userid") Long userid);
	
	@Query(value = "SELECT * from availability  where date >= CURDATE() ",nativeQuery = true)
	ArrayList<Availability> getAvailablityAllandDategraterthanToday();
	
	@Query(value = "SELECT * from availability  where userId = :userid AND  date BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d')  AND STR_TO_DATE(:endDate, '%Y-%m-%d')",nativeQuery = true)
	public ArrayList<Availability> getAllBetweenDates(@Param("userid")long userid, @Param("startDate")String startDate,@Param("endDate")String endDate);
	
	@Query(value = "SELECT * from availability  where  date BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d')  AND STR_TO_DATE(:endDate, '%Y-%m-%d')",nativeQuery = true)
	public ArrayList<Availability> getAllusersBetweenDates(@Param("startDate")String startDate,@Param("endDate")String endDate);

}
