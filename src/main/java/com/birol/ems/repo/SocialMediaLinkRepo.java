package com.birol.ems.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.birol.ems.dto.SocialMediaLinksDTO;

public interface SocialMediaLinkRepo extends JpaRepository<SocialMediaLinksDTO, String> {

}
