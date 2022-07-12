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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.lang.reflect.ParameterizedType;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;


@Repository
@Transactional
public abstract class GenericRepositoryClass<T extends IEntity, S extends Serializable> implements GenericRepository<T, S>
{
    @Autowired
    protected SessionFactory sessionFactory;

    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;

    protected Class<T> entityClass;

    @SuppressWarnings({"unchecked"})  // Remove warning about safe and unsafe static types.
    public GenericRepositoryClass()
    {
        this.entityClass = (Class<T>)(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    protected Session currentSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    public T saveOrUpdate(T entity) {
        // Session init {
        Session session = this.sessionFactory.openSession();
        session.beginTransaction();
        // }

        session.saveOrUpdate(entity);

        // Session close {
        session.getTransaction().commit();
        session.close();
        // }

        return entity;
    }

    @Override
    public T update(T entity, S id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        // Obtain the entity object that has the received ID
        T t = entityManager.find(this.entityClass, id);


        // If the entity manager is null it means there is no such element with this ID.
        if (t == null)
        {
            // Do nothing and show an exception because it is not found
            entityManager.close();
        }
        else
        {
            // If not, take the received data from the entity T and merge it into the data that we just received from
            // doing the getBy ID from the DB
            entityManager.getTransaction().begin();

            // Set the ID
            entity.setUUid(t.getUUid());

            // Merge into the entity that is being managed t, so it overwrites its fields
            entityManager.merge(entity);

            entityManager.getTransaction().commit();
            entityManager.close();
        }
        return t;
    }

    @Override
    public void add(T entity) {
        this.saveOrUpdate(entity);
    }

    @Override
    public T get(final S id) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        // Obtain the entity object that has the received ID (null in the cases where does nt exist)
        T t = entityManager.find(this.entityClass, id);

        entityManager.close();
        return t;
    }

    @Override
    public Set<T> getAll() {
        // Session init {
        Session session = this.sessionFactory.openSession();
        session.beginTransaction();
        // }

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(this.entityClass);
        criteria.from(this.entityClass);
        Set<T> res = new HashSet<>(session.createQuery(criteria).getResultList());

        // Session close {
        session.getTransaction().commit();
        session.close();
        // }
        return res;
    }

    @Modifying
    @Transactional
    @Override
    public int remove(S id){
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        T t = entityManager.find(this.entityClass, id);

        if (t == null)
        {
            entityManager.close();
            return 1;
        }
        else
        {
            entityManager.remove(t);
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.commit();
            entityManager.close();
            return 0;
        }
    }

    @Override
    public void clear() {
        this.currentSession().clear();
    }

    @Override
    public void flush() {
        this.currentSession().flush();
    }

    @Override
    public Class<T> getEntityClass() {
        return this.entityClass;
    }

}
