/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Researcher;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;


@Repository
@Transactional
public class ResearcherRepositoryClass extends GenericRepositoryClass<Researcher, UUID> implements ResearcherRepository {

    //RF this using spring boot and not raw queries to the database
    @Override
    public Optional<Researcher> getResearcherByEmail(String email) {
        return (Optional<Researcher>) super.currentSession().createQuery("FROM researcher where email=:email").setParameter("email", email).uniqueResult();
    }


}
