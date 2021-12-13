package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.repositories.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Service
public class GenericServiceClass<T, S extends Serializable> implements GenericService<T, S> {

    @Autowired
    private GenericRepository<T, S> genericRepository;

    public GenericServiceClass(GenericRepository<T, S> genericDao) {
        this.genericRepository = genericDao;
    }

    @Override
    @Transactional
    public T update(T entity, S id) {
        return this.genericRepository.update(entity, id);
    }

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
        return this.genericRepository.get(id);
    }

    @Override
    @Transactional
    public void add(T entity) {
        this.genericRepository.add(entity);
    }

    @Override
    @Transactional
    public int remove(S id) {
        return this.genericRepository.remove(id);
    }
}
