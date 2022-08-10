/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Researcher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * When extending a JPARepository interface (by extending GenericRepository) DB access methods are automatically
 * IMPLEMENTED regarding the bounded class, the name of the class and / or the name of the method.
 * JPA tries to deduce the suitable implementation by processing the natural language of the class or methods to
 * implement.
 */
@Repository
public interface ResearcherRepository<T extends IEntity, S extends Serializable> extends GenericRepository<Researcher, UUID>{

    Researcher findByUsername(String username);
}
