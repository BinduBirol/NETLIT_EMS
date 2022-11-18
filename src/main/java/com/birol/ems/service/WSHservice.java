package com.birol.ems.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.birol.ems.timereport.re.Time_Report_DTO;

@Service
public class WSHservice {

	public List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {

		List<LocalDate> totalDates = new ArrayList<>();
		while (!startDate.isAfter(endDate)) {
			totalDates.add(startDate);
			startDate = startDate.plusDays(1);
		}
		return totalDates;
	}

	
	
	
	
	
	public String generateTimeReportID(Time_Report_DTO ewsh, int index) {
		String id = "TR"+ewsh.getEmpid() + ewsh.getDate().toString().replace("-", "")+"_"+index;
		return id;
	}
	
	
	
	public String mintsTOHmConvert(long t) {
		long hours = t / 60;
		long minutes = t % 60;		
		return new String().format("%d Hours %02d Minutes", hours, minutes);
	}

}
