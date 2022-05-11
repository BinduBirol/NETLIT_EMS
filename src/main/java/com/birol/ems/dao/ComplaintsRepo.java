package com.birol.ems.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.birol.persistence.model.Complaints;

public interface ComplaintsRepo extends JpaRepository<Complaints, String> {
	
	Complaints findCmpById(int id);

}
