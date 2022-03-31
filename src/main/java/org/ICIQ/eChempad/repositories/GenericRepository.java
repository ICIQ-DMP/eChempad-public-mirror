/**
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

    T update(T entity, S id);

    T get(S id);

    Set<T> getAll();

    void clear();

    void flush();

    int remove(S id);

    Class<T> getEntityClass();

    // void remove(T entity);  // We will select the entities using the UUID, there is no point in sending ALL data
    // of an instance in order to remove it.

}
