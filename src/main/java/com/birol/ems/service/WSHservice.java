package com.birol.ems.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.birol.ems.dto.Employee_work_schedule;

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

	public String generateWShID(Employee_work_schedule ewsh) {
		String id = ewsh.getUserid() + ewsh.getDate().replace("-", "");
		return id;
	}

}
