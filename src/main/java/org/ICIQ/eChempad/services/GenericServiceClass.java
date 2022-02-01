package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.exceptions.ExceptionResourceNotExists;
import org.ICIQ.eChempad.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Services throw custom exceptions when they catch a spring exception or a custom exception. In a certain manner they
 * translate spring exceptions to a common preset of custom exceptions and forward these exceptions to the controller.
 * Services will have try-catch in the methods, especially to capture built-in exceptions and throw instead a custom
 * exception. Custom exceptions will be attended in the controller if needed, or not attended and implicitly forwarded
 * to the ExceptionHandlerGlobal class, which will have handlers for these custom exceptions.
 *
 * Services will also have the logic to know if someone can access a certain resource, they will have the business logic
 * of the permissions, so the most probable is that we will have many service classes not related exactly to the entity,
 * instead, they will have the different repositories to be able to manipulate the necessary data in the application.
 *
 * @param <T> Generic Entity type
 * @param <S> Serializable identifier, usually and UUID
 */
@Service
public class GenericServiceClass<T, S extends Serializable> implements GenericService<T, S> {

    protected GenericRepository<T, S> genericRepository;

    public GenericServiceClass() {}

    public GenericServiceClass(ResearcherRepository researcherRepository) {
        this.genericRepository = (GenericRepository<T, S>) researcherRepository;
    }

    public GenericServiceClass(JournalRepository journalRepository) {
        this.genericRepository = (GenericRepository<T, S>) journalRepository;
    }

    public GenericServiceClass(ExperimentRepository experimentRepository) {
        this.genericRepository = (GenericRepository<T, S>) experimentRepository;
    }

    public GenericServiceClass(DocumentRepository documentRepository) {
        this.genericRepository = (GenericRepository<T, S>) documentRepository;
    }

    @Override
    @Transactional
    public T update(T entity, S id) throws ExceptionResourceNotExists {
        T t = this.genericRepository.update(entity, id);
        if (t == null)
        {
            throw new ExceptionResourceNotExists("The resource of type " + entity.getClass().getName() + " with ID " + id.toString() + " does not exist.");
        }
        else
        {
            return t;
        }
    }

    @Override
    @Transactional
    public T saveOrUpdate(T entity) {
        return this.genericRepository.saveOrUpdate(entity);
    }

    @Override
    @Transactional
    public Set<T> getAll() {
        System.out.print(this.genericRepository.getAll().toString());

        return this.genericRepository.getAll();
    }

    @Override
    @Transactional
    public T get(S id) throws ExceptionResourceNotExists {
        T t = this.genericRepository.get(id);
        if (t == null)
        {
            throw new ExceptionResourceNotExists("The resource of type " + this.genericRepository.getEntityClass() + " with ID " + id.toString() + " does not exist.");
        }
        else
        {
            return t;
        }
    }

    @Override
    @Transactional
    public void add(T entity) {
        this.genericRepository.add(entity);

    }

    @Override
    @Transactional
    public void remove(S id) throws ExceptionResourceNotExists {
        int removeResult = this.genericRepository.remove(id);
        if (removeResult > 0)
        {
            // Returned 1, element could not have been deleted
            throw new ExceptionResourceNotExists("The resource of type " + this.genericRepository.getEntityClass() + " with ID " + id.toString() + " does not exist.");
        }
    }
}
