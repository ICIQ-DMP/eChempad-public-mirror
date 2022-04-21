/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.repositories;

import java.io.Serializable;
import java.util.Set;

/**
 * Alternative usage of CrudRepository
 * Code modified from http://www.codesenior.com/en/tutorial/Spring-Generic-DAO-and-Generic-Service-Implementation
 *
 * This interfaces defines basic generic methods for all repositories / entities, in order to modify the database.
 * @author malvarez
 *
 * @param <T> Generic entity.
 * @param <S> Generic primary key (data for unique identification).
 */

public interface GenericRepository<T, S extends Serializable> //extends JpaRepository<Researcher, Long> {
{

    void add(T entity);

    T saveOrUpdate(T entity);

    /**
     * Update an existing instance. We need the id to perform the update of the entity.
     * @param entity
     */
    T update(T entity, S id);

    T get(S id);

    Set<T> getAll();

    void clear();

    void flush();

    int remove(S id);

    /**
     * return the entity class of this generic repository.
     * @return Internal class type, set at the creation of the repository.
     */
    Class<T> getEntityClass();

}
