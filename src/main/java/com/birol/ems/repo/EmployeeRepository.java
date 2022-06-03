package com.birol.ems.repo;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.birol.ems.dto.EMPLOYEE_BASIC;




public interface EmployeeRepository extends CrudRepository<EMPLOYEE_BASIC, String> {
	
	String getbyroleQ= "SELECT * FROM employee_basic WHERE roleid like ?1  ";
	@Query(value =getbyroleQ,nativeQuery = true)
	ArrayList<EMPLOYEE_BASIC> findbyrole( int roleid);
	
	String getbyidQ= "SELECT * FROM employee_basic WHERE userid = ?1  ";
	@Query(value =getbyidQ,nativeQuery = true)
	EMPLOYEE_BASIC findbyUserid(Long id);
	
	String getbyempidQ= "SELECT * FROM employee_basic WHERE empid = ?1  ";
	@Query(value =getbyempidQ,nativeQuery = true)
	EMPLOYEE_BASIC findbyEmpid(Long id);
	
	String getbyemailQ= "SELECT * FROM employee_basic WHERE email = ?1  ";
	@Query(value =getbyemailQ,nativeQuery = true)
	EMPLOYEE_BASIC findbyWorkMail(String mail);
	
	String getbyStatusQ= "SELECT * FROM employee_basic WHERE status = ?1  ";
	@Query(value =getbyStatusQ,nativeQuery = true)
	ArrayList<EMPLOYEE_BASIC> findbyStatus(int s);
	
	String getlatestQ= "SELECT * FROM employee_basic order by created desc limit 5 ";
	@Query(value =getlatestQ,nativeQuery = true)
	ArrayList<EMPLOYEE_BASIC> findlatest();
}
