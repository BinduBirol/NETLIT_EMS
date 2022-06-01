package com.birol.ems.dao;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.birol.ems.dto.EmpTimeReportDTO;
import com.birol.ems.dto.Time_report_approved;

public interface EmpTimeReportRepo extends JpaRepository<EmpTimeReportDTO, String> {

	@Query(value = "SELECT * from EMP_TIME_REPORT  where userId = :userid AND  date BETWEEN CURDATE()-6 AND CURDATE()",nativeQuery = true)
	ArrayList<EmpTimeReportDTO> getAvailablityByuseridandDatelessthanToday(@Param("userid") Long userid);
	
	@Query(value = "SELECT * from EMP_TIME_REPORT  where date = CURDATE() ",nativeQuery = true)
	ArrayList<EmpTimeReportDTO> getAvailablityAllandDateToday();
	
	@Query(value = "SELECT * from EMP_TIME_REPORT  where userId = :userid AND  date BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d')  AND STR_TO_DATE(:endDate, '%Y-%m-%d') ",nativeQuery = true)
	public ArrayList<EmpTimeReportDTO> getAllBetweenDates(@Param("userid")long userid, @Param("startDate")String startDate,@Param("endDate")String endDate);
	
	@Query(value = "SELECT * from EMP_TIME_REPORT  where  date BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d')  AND STR_TO_DATE(:endDate, '%Y-%m-%d') ",nativeQuery = true)
	public ArrayList<EmpTimeReportDTO> getAllusersBetweenDates(@Param("startDate")String startDate,@Param("endDate")String endDate);

}
