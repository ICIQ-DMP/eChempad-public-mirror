/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.UUID;

@Repository
@Transactional
public class JournalRepositoryClass extends GenericRepositoryClass<Journal, UUID> implements JournalRepository{

    public JournalRepositoryClass() {
    }

    public String getDescription(UUID id) {
        return (String) super.currentSession().createQuery("FROM journal where id=:id").setParameter("id", id).uniqueResult();
    }

    public String getName(UUID id) {
        return (String) super.currentSession().createQuery("FROM researcher where id=:id").setParameter("id", id).uniqueResult();
    }

}
