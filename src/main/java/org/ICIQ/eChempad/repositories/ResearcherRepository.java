/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

public interface ResearcherRepository extends GenericRepository<Researcher, UUID>{

    UUID getIdByEmail(String email);

    String getEmail(UUID id);

    String getFullName(UUID id);

    String getSignalsAPIKey(UUID id);

    Optional<Researcher> getResearcherByEmail(String email);
}
