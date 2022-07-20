package com.birol.ems.timereport.repo;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.birol.ems.dto.EmpTimeReportDTO;
import com.birol.ems.timereport.dto.Timereport_Overtime_emp;

public interface EmpOvertimeRepo extends CrudRepository<Timereport_Overtime_emp, String>{

	@Query(value = "SELECT * from Timereport_Overtime_emp  where  date BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d')  AND STR_TO_DATE(:endDate, '%Y-%m-%d') and isapproved=0 and isrejected=0 order by week asc, date asc",nativeQuery = true)
	public ArrayList<Timereport_Overtime_emp> getAllusersBetweenDates(@Param("startDate")String startDate,@Param("endDate")String endDate);
}
