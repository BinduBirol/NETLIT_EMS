package com.birol.ems.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.birol.ems.dto.EmpTimeReportDTO;


public interface EmpTimeReportRepo extends JpaRepository<EmpTimeReportDTO, String> {

	@Query(value = "SELECT * from emp_time_report  where userId = :userid AND  date BETWEEN CURDATE()-6 AND CURDATE()",nativeQuery = true)
	ArrayList<EmpTimeReportDTO> getAvailablityByuseridandDatelessthanToday(@Param("userid") Long userid);
	
	@Query(value = "SELECT * from emp_time_report  where userId = :userid AND  date <= CURDATE()   order by week asc, date asc",nativeQuery = true)
	ArrayList<EmpTimeReportDTO> getAvailablityByuseridDatelessthanToday(@Param("userid") Long userid);
	
	@Query(value = "SELECT * from emp_time_report  where userId = :userid AND  date <= CURDATE() and isapproved=1 and isrejected=0 order by week asc, date asc",nativeQuery = true)
	ArrayList<EmpTimeReportDTO> getApprovedTimeReport(@Param("userid") Long userid);
	
	@Query(value = "SELECT * from emp_time_report  where date <= CURDATE() and isapproved=0 and isrejected=0 order by week asc, date asc",nativeQuery = true)
	ArrayList<EmpTimeReportDTO> getAvailablityAllandDateToday();
	
	@Query(value = "SELECT * from emp_time_report  where userId = :userid AND  date BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d')  AND STR_TO_DATE(:endDate, '%Y-%m-%d') order by week asc, date asc ",nativeQuery = true)
	public ArrayList<EmpTimeReportDTO> getApprovedBetweenDates(@Param("userid")long userid, @Param("startDate")String startDate,@Param("endDate")String endDate);
	
	@Query(value = "SELECT * from emp_time_report  where userId = :userid AND  date BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d')  AND STR_TO_DATE(:endDate, '%Y-%m-%d') ",nativeQuery = true)
	public ArrayList<EmpTimeReportDTO> getAllBetweenDates(@Param("userid")long userid, @Param("startDate")String startDate,@Param("endDate")String endDate);
	
	@Query(value = "SELECT * from emp_time_report  where userId = :userid AND  date BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d')  AND STR_TO_DATE(:endDate, '%Y-%m-%d') order by week asc, date asc",nativeQuery = true)
	public ArrayList<EmpTimeReportDTO> getUserBetweenDates(@Param("userid")long userid, @Param("startDate")String startDate,@Param("endDate")String endDate);
	
	
	@Query(value = "SELECT * from emp_time_report  where  date BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d')  AND STR_TO_DATE(:endDate, '%Y-%m-%d') and isapproved=0 and isrejected=0 order by week asc, date asc",nativeQuery = true)
	public ArrayList<EmpTimeReportDTO> getAllusersBetweenDates(@Param("startDate")String startDate,@Param("endDate")String endDate);
	
	@Query(value = "SELECT * from emp_time_report  where projectid = :projectid ",nativeQuery = true)
	public ArrayList<EmpTimeReportDTO> findbyProjectid(@Param("projectid")long projectid);
	
	@Query(value = "SELECT * from emp_time_report  where projectid = :projectid and isapproved=0 and isrejected=0",nativeQuery = true)
	public ArrayList<EmpTimeReportDTO> findbyProjectidAndApproved(@Param("projectid")long projectid);
	
	@Query(value = "SELECT * from emp_time_report  where userid = :userid and date=:date and isapproved=0 and isrejected=0",nativeQuery = true)
	public EmpTimeReportDTO findbyOnedateEmpid(@Param("userid")long userid, @Param("date")LocalDate date);	
	
	@Query(value = "SELECT * from emp_time_report  where  date=:date and isapproved=0 and isrejected=0 order by week asc, date asc",nativeQuery = true)
	public ArrayList<EmpTimeReportDTO> getAllusersOneDate(@Param("date")LocalDate date);	
	
	@Query(value = "SELECT * from emp_time_report  where userid = :userid and date=:date",nativeQuery = true)
	public EmpTimeReportDTO findbyOnedateEmpidanytype(@Param("userid")long userid, @Param("date")LocalDate date);

	@Query(value = "SELECT * from emp_time_report  where userid = :userid and date BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d')  AND STR_TO_DATE(:endDate, '%Y-%m-%d') and isapproved=1 order by week asc, date asc",nativeQuery = true)
	ArrayList<EmpTimeReportDTO> approvedOneUserFdTd(@Param("startDate")String from_date, @Param("endDate")String to_date, @Param("userid")Long id);	
	
	
	
	

}
