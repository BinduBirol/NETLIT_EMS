package com.birol.ems.repo;

import org.springframework.data.repository.CrudRepository;

import com.birol.ems.dto.SendMessage;

public interface ChatRepo extends CrudRepository<SendMessage, String> {

}
