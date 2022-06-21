package com.birol.ems.timereport.repo;

import org.springframework.data.repository.CrudRepository;

import com.birol.ems.timereport.dto.Timereport_Overtime_emp;

public interface EmpOvertimeRepo extends CrudRepository<Timereport_Overtime_emp, Integer>{

}
