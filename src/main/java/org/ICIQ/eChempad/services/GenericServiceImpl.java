/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.repositories.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Contains all the main methods implemented by the generic repository to manipulate the database. All the methods
 * provided by this interface are agnostic of the security or other special cases in concrete services. As thus, they
 * work as a wrap for the tables in the database in the logic layer. Other classes as SecurityService will be the ones
 * who manipulate this services and embed certain logic around it.
 *
 * Services throw custom exceptions when they catch a spring exception or a custom exception. In a certain manner they
 * translate spring exceptions to a common preset of custom exceptions and forward these exceptions to the controller.
 * Services will have try-catch in the methods, especially to capture built-in exceptions and throw instead a custom
 * exception. Custom exceptions will be attended in the controller if needed, or not attended and implicitly forwarded
 * to the ExceptionHandlerGlobal class, which will have handlers for these custom exceptions.
 *
 * Services will also have the logic to know if someone can access a certain resource, they will have the business logic
 * of the permissions, so the most probable is that we will have many service classes not related exactly to the entity,
 * instead, they will have the different repositories to be able to manipulate the necessary data in the application.

 * @param <T> Actual type, such as Experiment, Journal, etc.
 * @param <S> Serializable type used to identify the T type inside the database, usually a UUID
 *
 * Contains all methods delegated to the genericRepository class and a genericRepository. This class can be extended 
 * bounding an entity to an inheriting class in order to add the necessary business logic in the application.
 */
@Service
public abstract class GenericServiceImpl<T extends IEntity, S extends Serializable> implements GenericService<T, S>{

    protected GenericRepository<T, S> genericRepository;
    protected AclRepository aclRepository;

    //@PersistenceContext(type = PersistenceContextType.EXTENDED)
    //@Qualifier("sessionFactory")
    //private EntityManager entityManager;

    public GenericServiceImpl() {}

    public GenericServiceImpl(GenericRepository<T, S> repository, AclRepository aclRepository)
    {
        this.genericRepository = repository;
        this.aclRepository = aclRepository;
    }

    // Delegated methods to the repository

    public Class<T> getEntityClass() {
        return genericRepository.getEntityClass();
    }

    public List<T> findAll() {
        List<T> list = genericRepository.findAll();
        Logger.getGlobal().info(list.toString());
        return list;
    }

    public List<T> findAll(Sort sort) {
        return genericRepository.findAll(sort);
    }

    public List<T> findAllById(Iterable<S> s) {
        return genericRepository.findAllById(s);
    }

    public <S1 extends T> List<S1> saveAll(Iterable<S1> entities) {
        this.aclRepository.addGenericAclPermissions(this.getEntityClass(), BasePermission.ADMINISTRATION);
        return genericRepository.saveAll(entities);
    }

    public void flush() {
        genericRepository.flush();
    }

    public <S1 extends T> S1 saveAndFlush(S1 entity) {
        return genericRepository.saveAndFlush(entity);
    }

    public <S1 extends T> List<S1> saveAllAndFlush(Iterable<S1> entities) {
        return genericRepository.saveAllAndFlush(entities);
    }

    public void deleteAllInBatch(Iterable<T> entities) {
        genericRepository.deleteAllInBatch(entities);
    }

    public void deleteAllByIdInBatch(Iterable<S> s) {
        genericRepository.deleteAllByIdInBatch(s);
    }

    public void deleteAllInBatch() {
        genericRepository.deleteAllInBatch();
    }

    public T getById(S s) {
        T t = this.genericRepository.getById(s);
        t.toString();
        return t;
    }

    public <S1 extends T> List<S1> findAll(Example<S1> example) {
        return genericRepository.findAll(example);
    }

    public <S1 extends T> List<S1> findAll(Example<S1> example, Sort sort) {
        this.aclRepository.addGenericAclPermissions(this.getEntityClass(), BasePermission.ADMINISTRATION);
        return genericRepository.findAll(example, sort);
    }

    public Page<T> findAll(Pageable pageable) {
        return genericRepository.findAll(pageable);
    }

    public <S1 extends T> S1 save(S1 entity) {
        return genericRepository.save(entity);
    }

    public Optional<T> findById(S s) {
        return genericRepository.findById(s);
    }

    public boolean existsById(S s) {
        return genericRepository.existsById(s);
    }

    public long count() {
        return genericRepository.count();
    }

    public void deleteById(S s) {
        genericRepository.deleteById(s);
    }

    public void delete(T entity) {
        genericRepository.delete(entity);
    }

    public void deleteAllById(Iterable<? extends S> s) {
        genericRepository.deleteAllById(s);
    }

    public void deleteAll(Iterable<? extends T> entities) {
        genericRepository.deleteAll(entities);
    }

    public void deleteAll() {
        genericRepository.deleteAll();
    }

    public <S extends T> Optional<S> findOne(Example<S> example) {
        return genericRepository.findOne(example);
    }

    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return genericRepository.findAll(example, pageable);
    }

    public <S extends T> long count(Example<S> example) {
        return genericRepository.count(example);
    }

    public <S extends T> boolean exists(Example<S> example) {
        return genericRepository.exists(example);
    }
    
}
