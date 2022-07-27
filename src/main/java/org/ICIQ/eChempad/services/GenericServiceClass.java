/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.repositories.*;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GenericServiceClass<T extends IEntity, S extends Serializable> implements GenericService<T, S> {

    protected GenericRepository<T, S> genericRepository;

    public GenericServiceClass() {}

    public GenericServiceClass(GenericRepository<T, S> repository)
    {
        this.genericRepository = repository;
    }


    /**
     * calling save() on an object with predefined id will update the corresponding database record rather than
     * insert a new one
     * @return
     */
    public T update(T entity, S id) throws ResourceNotExistsException {
        // If we
        entity.setUUid((UUID) id);
        T t = this.genericRepository.save(entity);
        return entity;
    }

    @Override
    public T save(T entity) {
        return this.genericRepository.save(entity);
    }

    @Override
    public Set<T> get() {
        return new HashSet<>(this.genericRepository.findAll());
    }

    @Override
    public T get(S id) throws ResourceNotExistsException {
        Optional<T> entity = this.genericRepository.findById(id);
        if (entity.isPresent())
        {
            return entity.get();
        }
        else
        {
            throw new ResourceNotExistsException("The resource of type " + this.genericRepository.getEntityClass() + " with ID " + id.toString() + " does not exist.");
        }
    }

    @Override
    public void add(T entity) {
        this.genericRepository.save(entity);

    }

    @Override
    public void remove(S id) throws ResourceNotExistsException {
        if (this.genericRepository.findById(id).isPresent())
        {
            this.genericRepository.deleteById(id);
        }
        else
        {
            throw new ResourceNotExistsException("The resource of type " + this.genericRepository.getEntityClass() + " with ID " + id.toString() + " does not exist.");
        }
    }
}
