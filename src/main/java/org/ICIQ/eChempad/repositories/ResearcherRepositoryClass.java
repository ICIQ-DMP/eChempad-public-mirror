package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Researcher;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.UUID;


@Repository
@Transactional
public class ResearcherRepositoryClass extends GenericRepositoryClass<Researcher, UUID> implements ResearcherRepository {

    /**
     * We can use uniqueResult because we will not allow different users with the same e-mail.
     * @param email email of the user
     * @return
     */
    @Override
    public UUID getIdByEmail(String email) {
        return (UUID) super.currentSession().createQuery("FROM User where email=:email").setParameter("email", email).uniqueResult();
    }

    @Override
    public String getEmail(UUID id) {
        return (String) super.currentSession().createQuery("FROM User where id=:id").setParameter("id", id).uniqueResult();
    }

    @Override
    public String getFullName(UUID id) {
        return (String) super.currentSession().createQuery("FROM User where id=:id").setParameter("id", id).uniqueResult();
    }

    @Override
    public String getSignalsAPIKey(UUID id) {
        return (String) super.currentSession().createQuery("FROM User where id=:id").setParameter("id", id).uniqueResult();
    }
}
