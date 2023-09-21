package org.ICIQ.eChempad.services.genericJPAServices.SecuredServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Entity;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.genericJPAServices.GenericService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class SecuredGenericServiceImpl<T extends Entity, S extends Serializable> implements SecuredGenericService<T, S>{

    /**
     * Any service that fits the definition of generic service can be used by an inheriting class to protect it
     */
    GenericService<T, S> genericService;

    /**
     * Custom permission evaluator used for our ACL infrastructure
     */
    PermissionEvaluator permissionEvaluator;

    public SecuredGenericServiceImpl(GenericService<T, S> genericService, PermissionEvaluator permissionEvaluator) {
        this.genericService = genericService;
        this.permissionEvaluator = permissionEvaluator;
    }

    public SecuredGenericServiceImpl() {}

    @Override
    public Class<T> getEntityClass() {
        return this.genericService.getEntityClass();
    }

    @Override
    public <S1 extends T> S1 save(S1 entity) {
        if (this.genericService.existsById((S) entity.getId()))  // Exists in the db
        {
            if (this.permissionEvaluator.hasPermission(
                    SecurityContextHolder.getContext().getAuthentication(),
                    entity.getId(),
                    entity.getType().getCanonicalName(),
                    "WRITE")) {
                return this.genericService.save(entity);
            }
            else
            {
                throw new NotEnoughAuthorityException("Not enough authority to save object " + entity);
            }
        }
        else  // Does not exist, save directly
        {
            return this.genericService.save(entity);
        }
    }

    @Override
    public List<T> findAll() {
        return this.genericService.findAll().stream().filter(
                (T t) ->
                        this.permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), t.getId(), t.getTypeName(), "READ")
        )
                .collect(Collectors.toList());
    }

    @Override
    public Optional<T> findById(S s) {
        Optional<T> opt = this.genericService.findById(s);
        T t;
        if (opt.isPresent())
        {
            if (this.permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), opt.get().getId(), opt.get().getTypeName(), "READ"))
            {
                return opt;
            }
            else
            {
                throw new NotEnoughAuthorityException("Not enough authority to read object " + opt.get().getId());
            }
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(S s) {
        Optional<T> entity = this.genericService.findById(s);
        if (entity.isPresent()) {
            if (this.permissionEvaluator.hasPermission(
                    SecurityContextHolder.getContext().getAuthentication(),
                    s,
                    this.getEntityClass().getCanonicalName(),
                    "REMOVE"
            )) {
                this.genericService.deleteById(s);
            }
            else
            {
                throw new NotEnoughAuthorityException("Not enough authority to read entity " + s);
            }
        }
        else
        {
            throw new ResourceNotExistsException();
        }
    }


    // NOT IMPLEMENTED METHODS

    @Override
    public List<T> findAll(Sort sort) {
        return null;
    }

    @Override
    public List<T> findAllById(Iterable<S> s) {
        return null;
    }

    @Override
    public <S1 extends T> List<S1> saveAll(Iterable<S1> entities) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S1 extends T> S1 saveAndFlush(S1 entity) {
        return null;
    }

    @Override
    public <S1 extends T> List<S1> saveAllAndFlush(Iterable<S1> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<T> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<S> s) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public T getById(S s) {
        return null;
    }

    @Override
    public T getOne(S s) {
        return null;
    }



    @Override
    public <S1 extends T> List<S1> findAll(Example<S1> example) {
        return null;
    }

    @Override
    public <S1 extends T> List<S1> findAll(Example<S1> example, Sort sort) {
        return null;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public boolean existsById(S s) {
        return false;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(T entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends S> s) {

    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S1 extends T> Optional<S1> findOne(Example<S1> example) {
        return Optional.empty();
    }

    @Override
    public <S1 extends T> Page<S1> findAll(Example<S1> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S1 extends T> long count(Example<S1> example) {
        return 0;
    }

    @Override
    public <S1 extends T> boolean exists(Example<S1> example) {
        return false;
    }
}
