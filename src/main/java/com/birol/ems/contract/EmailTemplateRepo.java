package com.birol.ems.contract;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

public interface EmailTemplateRepo extends CrudRepository<Email_msg_DTO, Long> {

	ArrayList<Email_msg_DTO> findByEmpid(long e);
}
