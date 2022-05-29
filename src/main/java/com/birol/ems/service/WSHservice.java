package com.birol.ems.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.birol.ems.dao.EmpWSHrepo;
import com.birol.ems.dto.Availability;
import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.dto.Employee_work_schedule;

@Service
public class WSHservice {
	@Autowired
	EmpWSHrepo empWSHrepo;

	public List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {

		List<LocalDate> totalDates = new ArrayList<>();
		while (!startDate.isAfter(endDate)) {
			totalDates.add(startDate);
			startDate = startDate.plusDays(1);
		}
		return totalDates;
	}

	public String generateWShID(Employee_work_schedule ewsh) {
		String id = ewsh.getUserid() + ewsh.getDate().toString().replace("-", "");
		return id;
	}
	
	public String generateWavID(Availability ewsh) {
		String id = "AV"+ewsh.getUserid() + ewsh.getDate().toString().replace("-", "");
		return id;
	}
	
	public ArrayList<Employee_work_schedule> getEmployeeTimeReport(long empid) {
		ArrayList<Employee_work_schedule> empwsh= new ArrayList<Employee_work_schedule>();
		empwsh=(ArrayList<Employee_work_schedule>) empWSHrepo.findByUserid(empid);
		return empwsh;
	}
	
	public String mintsTOHmConvert(long t) {
		long hours = t / 60;
		long minutes = t % 60;		
		return new String().format("%d Hours %02d Minutes", hours, minutes);
	}

}
