package com.birol.ems.contract;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface NewDocRepo extends CrudRepository<NewDocumentForSign_DTO, Long>{

	@Query(value = "SELECT * FROM contract_info WHERE creator_id=?1 ORDER BY created desc", nativeQuery = true)
	ArrayList<NewDocumentForSign_DTO> findAllByCreator_idOrderByCreatedDesc(long empid);

}
