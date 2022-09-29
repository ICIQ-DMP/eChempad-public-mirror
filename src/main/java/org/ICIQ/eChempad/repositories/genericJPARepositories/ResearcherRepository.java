/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.repositories.genericJPARepositories;

import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.UUID;

@Repository
@Transactional
public interface ResearcherRepository<T extends JPAEntityImpl, S extends Serializable> extends GenericRepository<Researcher, UUID>{

    Researcher findByUsername(String username);
}
