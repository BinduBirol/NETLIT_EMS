package com.birol.ems.timereport.re;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

public interface Time_Report_Repo extends CrudRepository<Time_Report_DTO, String> {
	
	//public ArrayList<Time_Report_DTO> findByTr_id(String tr_id);

}
