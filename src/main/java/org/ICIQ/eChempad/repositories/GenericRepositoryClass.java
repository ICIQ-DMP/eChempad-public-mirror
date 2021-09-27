package org.ICIQ.eChempad.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.lang.reflect.ParameterizedType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class implements basic generic methods to make standard queries associated to all tables / entities.
 * @param <T>
 * @param <S>
 */
@Repository
@Transactional
public abstract class GenericRepositoryClass<T, S extends Serializable> implements GenericRepository<T, S> {

    @Autowired
    protected SessionFactory sessionFactory;

    protected Class<T> entityClass;


    @SuppressWarnings({"unchecked"})  // Remove warning about safe and unsafe static types.
    protected Session currentSession() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    public void add(T entity) {
        this.currentSession().save(entity);
    }

    @Override
    public T saveOrUpdate(T entity) {
        Session session = this.sessionFactory.openSession();
        session.beginTransaction();

        session.saveOrUpdate(entity);
        session.getTransaction().commit();
        session.close();

        return entity;
    }

    @Override
    public void update(T entity) {
        this.currentSession().saveOrUpdate(entity);
    }

    @Override
    public void remove(T entity) {
        this.currentSession().delete(entity);
    }

    @Override
    public T find(final S id) {
        return this.currentSession().get(this.entityClass, id);
    }

    @Override
    public Set<T> getAll() {
        Session session = this.sessionFactory.openSession();
        session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(this.entityClass);
        criteria.from(this.entityClass);
        Set<T> res = new HashSet<>(session.createQuery(criteria).getResultList());

        session.getTransaction().commit();
        session.close();
        return res;
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
