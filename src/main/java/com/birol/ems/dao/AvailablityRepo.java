package com.birol.ems.dao;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.birol.ems.dto.Availability;

public interface AvailablityRepo extends JpaRepository<Availability, String> {

	@Query(value = "SELECT * from availability  where userId = :userid AND  date >= CURDATE() ",nativeQuery = true)
	ArrayList<Availability> getAvailablityByuseridandDategraterthanToday(Long userid);

}
