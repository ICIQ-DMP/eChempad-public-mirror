package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.repositories.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Service
public abstract class GenericServiceImplementation<T, S extends Serializable> implements GenericService<T, S> {

    @Autowired
    private GenericRepository<T, S> genericRepository;

    @Override
    @Transactional
    public T saveOrUpdate(T entity) {
        return this.genericRepository.saveOrUpdate(entity);
    }

    @Override
    @Transactional
    public Set<T> getAll() {
        return this.genericRepository.getAll();
    }

    @Override
    @Transactional
    public T get(S id) {
        return this.genericRepository.find(id);
    }

    @Override
    @Transactional
    public void add(T entity) {
        this.genericRepository.add(entity);
    }

    @Override
    @Transactional
    public void update(T entity) {
        this.genericRepository.update(entity);
    }

    @Override
    @Transactional
    public void remove(T entity) {
        this.genericRepository.remove(entity);
    }
}
