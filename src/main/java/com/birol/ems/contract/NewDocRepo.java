package com.birol.ems.contract;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface NewDocRepo extends CrudRepository<NewDocumentForSign_DTO, Long>{

	List<NewDocumentForSign_DTO> findAllByOrderByCreatedDesc();

}
