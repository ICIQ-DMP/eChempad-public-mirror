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

    /**
     * We can use uniqueResult because we will not allow different users with the same e-mail.
     * @param email email of the user
     * @return UUID identifying the user.
     */
    @Override
    public UUID getIdByEmail(String email) {
        return (UUID) super.currentSession().createQuery("FROM researcher where email=:email").setParameter("email", email).uniqueResult();
    }

    @Override
    public String getEmail(UUID id) {
        return (String) super.currentSession().createQuery("FROM researcher where id=:id").setParameter("id", id).uniqueResult();
    }

    @Override
    public String getFullName(UUID id) {
        return (String) super.currentSession().createQuery("FROM researcher where id=:id").setParameter("id", id).uniqueResult();
    }

    @Override
    public String getSignalsAPIKey(UUID id) {
        return (String) super.currentSession().createQuery("FROM researcher where id=:id").setParameter("id", id).uniqueResult();
    }

    //RF this using spring boot and not raw queries to the database
    @Override
    public Optional<Researcher> getResearcherByEmail(String email) {
        return (Optional<Researcher>) super.currentSession().createQuery("FROM researcher where email=:email").setParameter("email", email).uniqueResult();
    }


}
