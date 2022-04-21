/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.repositories.*;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;

@Service
public class GenericServiceClass<T, S extends Serializable> implements GenericService<T, S> {

    protected GenericRepository<T, S> genericRepository;

    public GenericServiceClass() {}

    public GenericServiceClass(GenericRepository<T, S> repository)
    {
        this.genericRepository = repository;
    }

    @Override
    public T update(T entity, S id) throws ResourceNotExistsException {
        T t = this.genericRepository.update(entity, id);
        if (t == null)
        {
            throw new ResourceNotExistsException("The resource of type " + entity.getClass().getName() + " with ID " + id.toString() + " does not exist.");
        }
        else
        {
            return t;
        }
    }

    @Override
    public T save(T entity) {
        return this.genericRepository.saveOrUpdate(entity);
    }

    @Override
    public Set<T> get() {
        return this.genericRepository.getAll();
    }

    @Override
    public T get(S id) throws ResourceNotExistsException {
        T t = this.genericRepository.get(id);
        if (t == null)
        {
            throw new ResourceNotExistsException("The resource of type " + this.genericRepository.getEntityClass() + " with ID " + id.toString() + " does not exist.");
        }
        else
        {
            return t;
        }
    }

    @Override
    public void add(T entity) {
        this.genericRepository.add(entity);

    }

    @Override
    public void remove(S id) throws ResourceNotExistsException {
        int removeResult = this.genericRepository.remove(id);
        if (removeResult > 0)
        {
            // Returned 1, element could not have been deleted
            throw new ResourceNotExistsException("The resource of type " + this.genericRepository.getEntityClass() + " with ID " + id.toString() + " does not exist.");
        }
    }
}
