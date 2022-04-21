/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;

import java.io.Serializable;
import java.util.Set;

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
 * @param <S> Serializable type used to identify the T type inside the database, usually an UUID
 */
public interface GenericService<T, S extends Serializable> {

    /**
     * Saves the entity T to the database.
     * @param entity Generic entity. Contains unmanaged data by Spring Boot.
     * @return Managed entity.
     */
    T save(T entity);

    /**
     * Updates the generic entity with the supplied ID with the received data of a generic entity. Both entities have to
     * be of the same type.
     * @param entity Generic unmanaged entity.
     * @param id Serializable identifier for the entity that is going to be updated.
     * @return The generic entity that we passed as parameter now managed by Spring Boot.
     * @throws ResourceNotExistsException Thrown if the resource with the specified ID does not exist.
     */
    T update(T entity, S id) throws ResourceNotExistsException;

    /**
     * Obtain all entities of a type.
     * @return Collection of entities.
     */
    Set<T> get();

    /**
     * Obtains the generic entity T identified with the supplied ID.
     * @param id Identifier for the generic entity.
     * @return Returns the generic entity managed by Spring Boot.
     * @throws ResourceNotExistsException Thrown if the resource identified with the supplied ID does not exist.
     */
    T get(S id) throws ResourceNotExistsException;

    /**
     * Saves the entity T to the database.
     * @param entity Generic entity containing data to be saved.
     */
    void add(T entity);

    /**
     * Deletes an entity identified by the supplied ID.
     * @param id Identifies the entity that we want to delete.
     * @throws ResourceNotExistsException Thrown if the resource with the supplied ID does not exist.
     */
    void remove(S id) throws ResourceNotExistsException;

}

