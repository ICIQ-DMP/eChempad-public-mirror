/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

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
public interface GenericService<T extends JPAEntityImpl, S extends Serializable> extends JpaRepository<T, S> {

    /**
     * return the entity class of this generic repository.
     * Note: Default methods are a special Java 8 feature in where interfaces can define implementations for methods.
     * @return Internal class type of this generic repository, set at the creation of the repository.
     */
    public Class<T> getEntityClass();

    public List<T> findAll();

    public List<T> findAll(Sort sort);

    public List<T> findAllById(Iterable<S> s);

    public <S1 extends T> List<S1> saveAll(Iterable<S1> entities);

    public void flush();

    /**
     * Saves an entity and flushes changes instantly.
     *
     * @param entity entity to be saved. Must not be {@literal null}.
     * @return the saved entity
     */
    public <S1 extends T> S1 saveAndFlush(S1 entity);

    /**
     * Saves all entities and flushes changes instantly.
     *
     * @param entities entities to be deleted. Must not be {@literal null}.
     * @return the saved entities
     * @since 2.5
     */
    public <S1 extends T> List<S1> saveAllAndFlush(Iterable<S1> entities);

    /**
     * Deletes the given entities in a batch which means it will create a single query. This kind of operation leaves JPAs
     * first level cache and the database out of sync. Consider flushing the {@link EntityManager} before calling this
     * method.
     *
     * @param entities entities to be deleted. Must not be {@literal null}.
     * @since 2.5
     */
    public void deleteAllInBatch(Iterable<T> entities);

    /**
     * Deletes the entities identified by the given ids using a single query. This kind of operation leaves JPAs first
     * level cache and the database out of sync. Consider flushing the EntityManager before calling this method.
     *
     * @param s the ids of the entities to be deleted. Must not be {@literal null}.
     * @since 2.5
     */
    public void deleteAllByIdInBatch(Iterable<S> s);

    /**
     * Deletes all entities in a batch call.
     */
    public void deleteAllInBatch();

    /**
     * Returns a reference to the entity with the given identifier. Depending on how the JPA persistence provider is
     * implemented this is very likely to always return an instance and throw an
     * {@link EntityNotFoundException} on first access. Some of them will reject invalid identifiers
     * immediately.
     *
     * @param s must not be {@literal null}.
     * @return a reference to the entity with the given identifier.
     * @since 2.5
     */
    public T getById(S s);

    public <S1 extends T> List<S1> findAll(Example<S1> example);

    public <S1 extends T> List<S1> findAll(Example<S1> example, Sort sort);

    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable
     * @return a page of entities
     */
    public Page<T> findAll(Pageable pageable);

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity must not be {@literal null}.
     * @return the saved entity; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
     */
    public <S1 extends T> S1 save(S1 entity);

    /**
     * Retrieves an entity by its id.
     *
     * @param s must not be {@literal null}.
     * @return the entity with the given id or {@literal Optional#empty()} if none found.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    public Optional<T> findById(S s);

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param s must not be {@literal null}.
     * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    public boolean existsById(S s);

    /**
     * Returns the number of entities available.
     *
     * @return the number of entities.
     */
    public long count();

    /**
     * Deletes the entity with the given id.
     *
     * @param s must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     */
    public void deleteById(S s);

    /**
     * Deletes a given entity.
     *
     * @param entity must not be {@literal null}.
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    public void delete(T entity);

    /**
     * Deletes all instances of the type {@code T} with the given IDs.
     *
     * @param s must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@literal ids} or one of its elements is {@literal null}.
     * @since 2.5
     */
    public void deleteAllById(Iterable<? extends S> s);

    /**
     * Deletes the given entities.
     *
     * @param entities must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@literal entities} or one of its entities is {@literal null}.
     */
    public void deleteAll(Iterable<? extends T> entities);

    /**
     * Deletes all entities managed by the repository.
     */
    public void deleteAll();

    /**
     * Returns a single entity matching the given {@link Example} or {@literal null} if none was found.
     *
     * @param example must not be {@literal null}.
     * @return a single entity matching the given {@link Example} or {@link Optional#empty()} if none was found.
     * @throws IncorrectResultSizeDataAccessException if the Example yields more than one result.
     */
    public <S extends T> Optional<S> findOne(Example<S> example);

    /**
     * Returns a {@link Page} of entities matching the given {@link Example}. In case no match could be found, an empty
     * {@link Page} is returned.
     *
     * @param example must not be {@literal null}.
     * @param pageable can be {@literal null}.
     * @return a {@link Page} of entities matching the given {@link Example}.
     */
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable);

    /**
     * Returns the number of instances matching the given {@link Example}.
     *
     * @param example the {@link Example} to count instances for. Must not be {@literal null}.
     * @return the number of instances matching the {@link Example}.
     */
    public <S extends T> long count(Example<S> example);

    /**
     * Checks whether the data store contains elements that match the given {@link Example}.
     *
     * @param example the {@link Example} to use for the existence check. Must not be {@literal null}.
     * @return {@literal true} if the data store contains elements that match the given {@link Example}.
     */
    public <S extends T> boolean exists(Example<S> example);

}
