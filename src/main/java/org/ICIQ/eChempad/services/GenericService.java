package org.ICIQ.eChempad.services;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface GenericService<T, S extends Serializable> {

    T saveOrUpdate(T entity);

    T update(T entity, S id);

    Set<T> getAll();

    T get(S id);

    void add(T entity);

    int remove(S id);

}

