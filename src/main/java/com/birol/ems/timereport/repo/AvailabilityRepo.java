package com.birol.ems.timereport.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.birol.ems.timereport.dto.Availability_Type;

public interface AvailabilityRepo extends JpaRepository<Availability_Type, Integer>{

	Availability_Type findByTypename(String name);
}
