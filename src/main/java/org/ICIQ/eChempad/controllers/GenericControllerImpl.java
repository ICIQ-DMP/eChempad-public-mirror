package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.HashSet;
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
            value = "/getAll",
            produces = "application/json"
    )
    @Override
    public ResponseEntity<Set<T>> getAll(@PathVariable(value = "entityType") String entityType) {
        Logger.getGlobal().info("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
        HashSet<T> entities = new HashSet<>(this.genericService.findAll());
        return ResponseEntity.ok(entities);
    }

    @Override
    public ResponseEntity<T> get(S id) throws ResourceNotExistsException {
        return null;
    }

    @Override
    public ResponseEntity<T> add(T t) {
        return null;
    }

    @Override
    public ResponseEntity<T> remove(S id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        return null;
    }

    @Override
    public ResponseEntity<T> put(T t, S id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        return null;
    }
}
