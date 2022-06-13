package com.birol.ems.dao;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.birol.ems.dto.Time_report_approved;

public interface EmpWSHrepo extends JpaRepository<Time_report_approved, String> {
	String getwhbyUser= "SELECT * FROM time_report_approved WHERE userId = ?1  order by date desc";
	@Query(value =getwhbyUser,nativeQuery = true)
	ArrayList<Time_report_approved> findByUserid(long userid);
	
	String getuserwshbydate= "SELECT * FROM time_report_approved WHERE userId = ?1  and date=?2 ";
	@Query(value =getuserwshbydate,nativeQuery = true)
	Time_report_approved findByUserIdAndDate(long userid, Date d);
	
	String getuserwshbydaterange= "SELECT * FROM time_report_approved WHERE userId = ?1  and date between ?2 and ?3 ";
	@Query(value =getuserwshbydaterange,nativeQuery = true)
	ArrayList<Time_report_approved> findByUserIdAndDateRange(long userid, Date fd, Date td);
	
	@Query(value = "SELECT * from time_report_approved  where userId = :userid AND  date BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d')  AND STR_TO_DATE(:endDate, '%Y-%m-%d')",nativeQuery = true)
	public ArrayList<Time_report_approved> getAllBetweenDates(@Param("userid")long userid, @Param("startDate")String startDate,@Param("endDate")String endDate);
	
	@Query(value = "SELECT * from time_report_approved  where userId = :userid AND  date = CURDATE() ",nativeQuery = true)
	public ArrayList<Time_report_approved> getToday(@Param("userid")long userid);
	
	
	String deleteByAvid= "DELETE FROM time_report_approved WHERE availability_id = ?1 ";
	@Query(value =deleteByAvid,nativeQuery = true)
	String deleteByAvid(String availability_id);
	
	String getprojectTimereportq= "SELECT * FROM time_report_approved WHERE projectid = ?1  order by week desc, date desc";
	@Query(value =getprojectTimereportq,nativeQuery = true)
	ArrayList<Time_report_approved> findByProjectid(long projectid);
}
