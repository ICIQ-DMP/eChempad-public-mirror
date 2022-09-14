package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.GenericService;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public abstract class GenericControllerImpl<T extends IEntity, S extends Serializable> implements GenericController<T, S> {

    protected GenericService<T, S> genericService;

    public GenericControllerImpl(GenericService<T, S> genericService) {
        this.genericService = genericService;
    }

    @Override
    public Class<T> getEntityClass() {
        return this.genericService.getEntityClass();
    }

    @GetMapping(
            value = "",
            produces = "application/json"
    )
    @PostFilter("hasPermission(filterObject, 'READ')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public Set<T> getAll() {
        return new HashSet<>(this.genericService.findAll());
    }

    @GetMapping(
            value = "/{id}",
            produces = "application/json"
    )
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<T> get(@PathVariable S id) throws ResourceNotExistsException {
        Optional<T> opt = this.genericService.findById(id);

        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(
            value = "",
            produces = "application/json",
            consumes = "application/json"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<T> add(@Validated @RequestBody T t) {
        T entity = this.genericService.save(t);
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping(
            value = "/{id}",
            produces = "application/json"
    )
    @PreAuthorize("hasPermission(#id, @genericServiceImpl.getEntityClass(), 'REMOVE')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<T> remove(@PathVariable S id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        Optional<T> entity = this.genericService.findById(id);
        this.genericService.deleteById(id);
        if (entity.isPresent())
            return ResponseEntity.ok(entity.get());
        else
            throw new ResourceNotExistsException();
    }

    @PutMapping(
            value = "/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    @PreAuthorize("hasPermission(#id, #t.myType, 'WRITE')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<T> put(@Validated @RequestBody T t, @PathVariable(value = "id") S id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        t.setId(id);
        T entity = this.genericService.save(t);
        return ResponseEntity.ok(entity);
    }

}
