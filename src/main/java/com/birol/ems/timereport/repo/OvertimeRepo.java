package com.birol.ems.timereport.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.birol.ems.timereport.dto.Overtime_Type;

public interface OvertimeRepo extends JpaRepository<Overtime_Type, Integer>{

	Overtime_Type findByTypename(String name);
}
