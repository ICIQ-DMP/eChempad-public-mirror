/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Researcher;

import java.util.Optional;
import java.util.UUID;

public interface ResearcherRepository extends GenericRepository<Researcher, UUID>{

    Optional<Researcher> getResearcherByEmail(String email);
}
