/**
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
 * @param <T> Actual type, such as Experiment, Journal, etc.
 * @param <S> Serializable type used to identify the T type inside the database.
 */
public interface GenericService<T, S extends Serializable> {

    T save(T entity);

    T update(T entity, S id) throws ResourceNotExistsException;

    Set<T> getAll();

    T get(S id) throws ResourceNotExistsException;

    void add(T entity);

    void remove(S id) throws ResourceNotExistsException;

}

