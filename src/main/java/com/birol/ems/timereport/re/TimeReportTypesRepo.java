package com.birol.ems.timereport.re;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TimeReportTypesRepo extends CrudRepository<TimeReportTypesDTO, Integer> {
	TimeReportTypesDTO findByTypename(String name);
	ArrayList<TimeReportTypesDTO> findByisWorking(boolean b);
	ArrayList<TimeReportTypesDTO> findByisOB(boolean b);
	
	ArrayList<TimeReportTypesDTO> findAllByOrderByIsWorkingDescTypeidAsc();
}
