package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Researcher;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.orm.hibernate5.HibernateOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

/**
 * This class implements basic generic methods to make standard queries associated to all tables / entities.
 * @param <T>
 * @param <S>
 */
@Repository
@Transactional
public abstract class GenericRepositoryClass<T, S extends Serializable> implements GenericRepository<T, S> //, implements CrudRepository<T, S>,
{

    // autowired sessionFactory magically obtained (not explicitly set, adequate set deducted from hibernate conf)
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
    public void add(T entity) {
        this.saveOrUpdate(entity);
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
    public void update(T entity) {
        this.saveOrUpdate(entity);
    }

    @Override
    public void remove(T entity) {
        this.currentSession().delete(entity);
    }

    @Override
    public T get(final S id) {
        // Session init {
        Session session = this.sessionFactory.openSession();
        session.beginTransaction();
        // }
        // Logger.getLogger("LO MAIN").info("in generic is one as it hsould" );

        T res = session.get(this.entityClass, id);

        // Session close {
        session.getTransaction().commit();
        session.close();
        // }
        return res;
    }

    @Override
    public Set<T> getAll() {
        // Session init {
        Session session = this.sessionFactory.openSession();
        session.beginTransaction();
        // }
        // Logger.getLogger("LO MAIN").info("in generic is all" );

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
        /* TRY1
        // Session init {
        Session session = this.sessionFactory.openSession();
        session.beginTransaction();
        // }

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaDelete<T> criteria = builder.createCriteriaDelete(this.entityClass);

        Root<T> entity = criteria.from(this.entityClass);
        Predicate idMatch = builder.equal(entity.get("id"), id);

        criteria.where(idMatch);

        // Assume unique id //RF
        T t = (T) session.createQuery(criteria).getResultList();
        int res;
        if (t == null)
        {
            res = 0;
        }
        else
        {
            res = 1;
        }

        // Session close {
        session.getTransaction().commit();
        session.close();
        // }

        return res;
        */

        /* TRY2
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();


        T t = null;
        try {
            Constructor<T> constructor = this.entityClass.getConstructor(UUID.class);
            UUID uuid = UUID.fromString(id.toString());
            T ghost = constructor.newInstance(uuid);
            t = entityManager.find(this.entityClass, ghost);

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        entityManager.remove(t);


        entityManager.getTransaction().commit();
        entityManager.close();
        return 0;
        */

        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        T t = entityManager.find(this.entityClass, id);



        if (t == null)
        {
            return 1;
        }
        else
        {
            entityManager.remove(t);
            entityManager.getTransaction().commit();
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





}
