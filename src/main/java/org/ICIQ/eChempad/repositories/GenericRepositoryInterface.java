package org.ICIQ.eChempad.repositories;

import java.io.Serializable;
import java.util.List;

/**
 * Alternative usage of CrudRepository
 * Code modified from http://www.codesenior.com/en/tutorial/Spring-Generic-DAO-and-Generic-Service-Implementation
 * @author malvarez
 *
 * @param <T>
 * @param <S>
 */

public interface GenericRepositoryInterface<T, S extends Serializable> {

    void add(T entity);

    void saveOrUpdate(T entity);

    void update(T entity);

    void remove(T entity);

    T find(S id);

    List<T> getAll();

    void clear();

    void flush();

}
