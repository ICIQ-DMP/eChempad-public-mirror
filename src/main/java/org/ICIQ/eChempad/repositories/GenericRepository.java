package org.ICIQ.eChempad.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.lang.reflect.ParameterizedType;
import java.io.Serializable;
import java.util.List;

@Repository
@Transactional
public abstract class GenericRepository<T, S extends Serializable> implements GenericRepositoryInterface<T, S> {

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
    public void saveOrUpdate(T entity) {
        this.currentSession().saveOrUpdate(entity);
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
    public T find(final Serializable id) {
        return this.currentSession().get(this.entityClass, id);
    }

    @Override
    public List<T> getAll() {
        CriteriaBuilder builder = this.currentSession().getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(this.entityClass);
        criteria.from(this.entityClass);
        return this.currentSession().createQuery(criteria).getResultList();
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
