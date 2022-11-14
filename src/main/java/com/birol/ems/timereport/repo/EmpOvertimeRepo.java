package com.birol.ems.timereport.repo;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.birol.ems.dto.EmpTimeReportDTO;
import com.birol.ems.timereport.dto.Timereport_Overtime_emp;

public interface EmpOvertimeRepo extends CrudRepository<Timereport_Overtime_emp, String>{

	@Query(value = "SELECT * from emp_time_report_overtime  where  date BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d')  AND STR_TO_DATE(:endDate, '%Y-%m-%d') and isapproved=0 and isrejected=0 order by week asc, date asc",nativeQuery = true)
	public ArrayList<Timereport_Overtime_emp> getAllusersBetweenDates(@Param("startDate")String startDate,@Param("endDate")String endDate);
	
	@Query(value = "SELECT * from emp_time_report_overtime  where  date=:date  and isapproved=0 and isrejected=0 order by week asc, date asc",nativeQuery = true)
	public ArrayList<Timereport_Overtime_emp> getAllusersOneDate(@Param("date")LocalDate date);	
	
	@Query(value = "SELECT * from emp_time_report_overtime  where userid = :userid and date=:date and isapproved=0 and isrejected=0",nativeQuery = true)
	public ArrayList<Timereport_Overtime_emp>  findbyOnedateEmpid(@Param("userid")long userid, @Param("date")LocalDate date);

	@Query(value = "SELECT * from emp_time_report_overtime  where userid = :userid and date BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d')  AND STR_TO_DATE(:endDate, '%Y-%m-%d') and isapproved=1",nativeQuery = true)
	public ArrayList<Timereport_Overtime_emp> approvedOneUserFdTd(@Param("startDate")String startDate, @Param("endDate")String endDate, @Param("userid")Long userid);

}
