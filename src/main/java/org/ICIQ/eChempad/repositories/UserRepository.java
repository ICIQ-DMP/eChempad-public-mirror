package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> findAll()
    {
        return this.entityManager.createQuery("from " + User.class.getName()).getResultList();

    }
}
