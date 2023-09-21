/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Entity;
import org.ICIQ.eChempad.repositories.genericJPARepositories.GenericRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;


@Service
public abstract class GenericServiceImpl<T extends Entity, S extends Serializable> implements GenericService<T, S>{

    /**
     * Persistence context of the class. This provides extended programmatic capabilities to access the database data.
     * We use {@code type= PersistenceContextType.EXTENDED} because it has been reported as a valid mechanism to keep
     * LOB descriptors without staling.
     */
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    protected EntityManager entityManager;

    /**
     * The repository that performs all the database manipulation.
     */
    protected GenericRepository<T, S> genericRepository;

    public GenericServiceImpl() {}

    public GenericServiceImpl(GenericRepository<T, S> repository)
    {
        this.genericRepository = repository;
    }

    public Class<T> getEntityClass() {
        return genericRepository.getEntityClass();
    }

    // Business methods: Contains the logic of the application

    /**
     * Saves entity and gives full permissions to the creator.
     *
     * @param entity must not be {@literal null}.
     * @param <S1> entity to be saved
     * @return entity that has been saved
     */
    public <S1 extends T> S1 save(S1 entity) {
        return genericRepository.save(entity);
    }

    /**
     * Returns all entities of a certain type T.
     *
     * @return List of entities
     */
    public List<T> findAll() {
        return genericRepository.findAll();
    }

    /**
     * Fins the entity of type T with a certain id
     * @param s must not be {@literal null}.
     * @return entity T wrapped in optional<T>
     */
    public Optional<T> findById(S s) {
        return genericRepository.findById(s);
    }

    // Decorated methods: Delegate and decorate method call to the repository

    public List<T> findAll(Sort sort) {
        return genericRepository.findAll(sort);
    }

    public List<T> findAllById(Iterable<S> s) {
        return genericRepository.findAllById(s);
    }

    public <S1 extends T> List<S1> saveAll(Iterable<S1> entities) {
        return genericRepository.saveAll(entities);
    }

    public void flush() {
        genericRepository.flush();
    }

    public <S1 extends T> S1 saveAndFlush(S1 entity) {
        return genericRepository.saveAndFlush(entity);
    }

    public <S1 extends T> List<S1> saveAllAndFlush(Iterable<S1> entities) {
        return genericRepository.saveAllAndFlush(entities);
    }

    public void deleteAllInBatch(Iterable<T> entities) {
        genericRepository.deleteAllInBatch(entities);
    }

    public void deleteAllByIdInBatch(Iterable<S> s) {
        genericRepository.deleteAllByIdInBatch(s);
    }

    public void deleteAllInBatch() {
        genericRepository.deleteAllInBatch();
    }

    public T getById(S s) {
        return this.genericRepository.getById(s);
    }

    public <S1 extends T> List<S1> findAll(Example<S1> example) {
        return genericRepository.findAll(example);
    }

    public <S1 extends T> List<S1> findAll(Example<S1> example, Sort sort) {
        return genericRepository.findAll(example, sort);
    }

    public Page<T> findAll(Pageable pageable) {
        return genericRepository.findAll(pageable);
    }

    public boolean existsById(S s) {
        return genericRepository.existsById(s);
    }

    public long count() {
        return genericRepository.count();
    }

    public void deleteById(S s) {
        genericRepository.deleteById(s);
    }

    public void delete(T entity) {
        genericRepository.delete(entity);
    }

    public void deleteAllById(Iterable<? extends S> s) {
        genericRepository.deleteAllById(s);
    }

    public void deleteAll(Iterable<? extends T> entities) {
        genericRepository.deleteAll(entities);
    }

    public void deleteAll() {
        genericRepository.deleteAll();
    }

    public <S1 extends T> Optional<S1> findOne(Example<S1> example) {
        return genericRepository.findOne(example);
    }

    public <S1 extends T> Page<S1> findAll(Example<S1> example, Pageable pageable) {
        return genericRepository.findAll(example, pageable);
    }

    public <S1 extends T> long count(Example<S1> example) {
        return genericRepository.count(example);
    }

    public <S1 extends T> boolean exists(Example<S1> example) {
        return genericRepository.exists(example);
    }

    @Override
    @Deprecated
    public @NotNull T getOne(@NotNull S s) {
        return null;
    }
}
