package com.birol.ems.timereport.re;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface Time_Report_Repo extends CrudRepository<Time_Report_DTO, String> {
	
	public ArrayList<Time_Report_DTO> findByDateAndIsapprovedAndIsrejected(LocalDate d,boolean a,boolean r);

	public ArrayList<Time_Report_DTO> findByDateAndEmpidAndIsapprovedAndIsrejected( LocalDate d, Long empid,boolean a,boolean r);

	public ArrayList<Time_Report_DTO> findByEmpid(Long id);
	
	public ArrayList<Time_Report_DTO> findByDateAndEmpid(LocalDate date, long empid);

	@Query(value = "SELECT * from time_report  "
			+ " where empid = :userid and date BETWEEN STR_TO_DATE(:startDate, '%Y-%m-%d')  "
			+ " AND STR_TO_DATE(:endDate, '%Y-%m-%d') and isapproved=1 and isrejected=0"
			+ " order by week asc, date asc",nativeQuery = true)
	public ArrayList<Time_Report_DTO> findApprovedByEmpidAndFromDateToDate(@Param("userid")Long id, 
			@Param("startDate")String from_date, 
			@Param("endDate")String to_date);

}
