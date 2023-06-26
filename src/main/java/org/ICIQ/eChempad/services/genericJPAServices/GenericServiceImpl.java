/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
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
import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;
import org.ICIQ.eChempad.repositories.genericJPARepositories.GenericRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;


@Service
public abstract class GenericServiceImpl<T extends EntityImpl, S extends Serializable> implements GenericService<T, S>{

    /**
     * Persistence context of the class. This provides extended programmatic capabilities to access the database data.
     * We use {@code type= PersistenceContextType.EXTENDED} because it has been reported as a valid mechanism to keep
     * LOB descriptors without staling.
     */
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    protected EntityManager entityManager;

    /**
     * The repository that performs all the database manipulation.
     */
    protected GenericRepository<T, S> genericRepository;

    /**
     * The repository that performs the database manipulation regarding security and ACL state.
     */
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
     * Saves entity and gives full permissions to the creator.
     *
     * @param entity must not be {@literal null}.
     * @param <S1> entity to be saved
     * @return entity that has been saved
     */
    public <S1 extends T> S1 save(S1 entity) {
        // Save the creation date of this entity.
        entity.initCreationDate();

        // Save it in the database
        S1 t = genericRepository.save(entity);

        // If the entity is a DataEntity, set inheriting false if the parent is null, set to true otherwise. If set to
        // true, the ACL of the parent has to be recovered.
        boolean inheriting = entity instanceof DataEntity && ((DataEntity) entity).getParent() != null;

        // Save all possible permission against the saved entity with the current logged user
        this.aclRepository.addPermissionToEntity(t, inheriting, PermissionBuilder.getFullPermissions(), null);

        return t;
    }

    /**
     * Returns all entities of a certain type T.
     *
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

    public T getById(S s) {
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
