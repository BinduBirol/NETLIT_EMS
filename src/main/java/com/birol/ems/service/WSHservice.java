package com.birol.ems.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.birol.ems.dao.EmpWSHrepo;
import com.birol.ems.dto.EmpTimeReportDTO;
import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.dto.Time_report_approved;

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

	public String generateWShID(Time_report_approved ewsh) {
		String id = ewsh.getUserid() + ewsh.getDate().toString().replace("-", "");
		return id;
	}
	
	public String generateWavID(EmpTimeReportDTO ewsh) {
		String id = "AV"+ewsh.getUserid() + ewsh.getDate().toString().replace("-", "");
		return id;
	}
	
	public ArrayList<Time_report_approved> getEmployeeTimeReport(long empid) {
		ArrayList<Time_report_approved> empwsh= new ArrayList<Time_report_approved>();
		empwsh=(ArrayList<Time_report_approved>) empWSHrepo.findByUserid(empid);
		return empwsh;
	}
	
	public String mintsTOHmConvert(long t) {
		long hours = t / 60;
		long minutes = t % 60;		
		return new String().format("%d Hours %02d Minutes", hours, minutes);
	}

}
