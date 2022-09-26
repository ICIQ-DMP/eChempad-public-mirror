package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.SecurityId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Component
@Configuration
public interface SecurityIdRepository extends JpaRepository<SecurityId, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO acl_sid (principal, sid) VALUES (:principal, :sid)", nativeQuery = true)
    void addAclRole(@Param("principal") boolean principal, @Param("sid") String sid);
}
