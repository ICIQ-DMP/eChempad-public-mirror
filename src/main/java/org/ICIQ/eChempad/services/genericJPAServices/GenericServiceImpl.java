/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.configurations.security.ACL.AclServiceCustomImpl;
import org.ICIQ.eChempad.configurations.security.ACL.PermissionBuilder;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.ICIQ.eChempad.repositories.genericJPARepositories.GenericRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;


@Service
public abstract class GenericServiceImpl<T extends JPAEntityImpl, S extends Serializable> implements GenericService<T, S>{

    @PersistenceContext
    protected EntityManager entityManager;

    protected GenericRepository<T, S> genericRepository;
    protected AclServiceCustomImpl aclRepository;

    public GenericServiceImpl() {}

    public GenericServiceImpl(GenericRepository<T, S> repository, AclServiceCustomImpl aclRepository)
    {
        this.genericRepository = repository;
        this.aclRepository = aclRepository;
    }

    public Class<T> getEntityClass() {
        return genericRepository.getEntityClass();
    }

    // Business methods: Contains the logic of the application

    /**
     * Saves entity and gives full permissions to the creator
     * @param entity must not be {@literal null}.
     * @param <S1> entity to be saved
     * @return entity that has been saved
     */
    public <S1 extends T> S1 save(S1 entity) {
        S1 t = genericRepository.save(entity);

        // Save all possible permission against the saved entity with the current logged user
        Iterator<Permission> iterator = PermissionBuilder.getFullPermissionsIterator();
        while (iterator.hasNext()) {
            this.aclRepository.addPermissionToUserInEntity(t, iterator.next());
        }

        return t;
    }

    /**
     * Returns all entities of a certain type T
     * @return List of entities
     */
    public List<T> findAll() {
        return genericRepository.findAll();
    }

    /**
     * Fins the entity of type T with a certain id
     * @param s must not be {@literal null}.
     * @return entity T wrapped in optional<T>
     */
    public Optional<T> findById(S s) {
        return genericRepository.findById(s);
    }

    // Decorated methods: Delegate and decorate method call to the repository

    public List<T> findAll(Sort sort) {
        return genericRepository.findAll(sort);
    }

    public List<T> findAllById(Iterable<S> s) {
        return genericRepository.findAllById(s);
    }

    public <S1 extends T> List<S1> saveAll(Iterable<S1> entities) {
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

    /**
     * Returns the entity uninitialized and causing a LazyInitializationException afterwards. Use findById instead.
     */
    public T getById(S s) {
        Logger.getGlobal().warning("WARNING! You are using getById which can cause a Lazy Initialization Exception " +
                "if used out of session, use getById to avoid this and load the full entity.");
        return this.genericRepository.getById(s);
    }

    public <S1 extends T> List<S1> findAll(Example<S1> example) {
        return genericRepository.findAll(example);
    }

    public <S1 extends T> List<S1> findAll(Example<S1> example, Sort sort) {
        return genericRepository.findAll(example, sort);
    }

    public Page<T> findAll(Pageable pageable) {
        return genericRepository.findAll(pageable);
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

    @Override
    @Deprecated
    public @NotNull T getOne(@NotNull S s) {
        return null;
    }
}
