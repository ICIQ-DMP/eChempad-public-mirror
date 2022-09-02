package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public class GenericControllerImpl<T extends IEntity, S extends Serializable> implements GenericController<T, S> {

    protected GenericService<T, S> genericService;

    public GenericControllerImpl(GenericService<T, S> genericService)
    {
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
    @Override
    public ResponseEntity<Set<T>> getAll() {
        HashSet<T> entities = new HashSet<>(this.genericService.findAll());

        return ResponseEntity.ok(entities);
    }

    @GetMapping(
            value = "/{id}",
            produces = "application/json"
    )
    @Override
    public ResponseEntity<T> get(@PathVariable S id) throws ResourceNotExistsException {
        T entity = this.genericService.getById(id);
        return ResponseEntity.ok(entity);
    }

    @PostMapping(
            value = "",
            produces = "application/json",
            consumes = "application/json"
    )
    @Override
    public ResponseEntity<T> add(@Validated @RequestBody T t) {
        T entity = this.genericService.save(t);
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping(
            value = "/{id}",
            produces = "application/json"
    )
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
    @Override
    public ResponseEntity<T> put(@Validated @RequestBody T t, @PathVariable(value = "id") S id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        t.setId(id);
        T entity = this.genericService.save(t);
        return ResponseEntity.ok(entity);
    }
}
