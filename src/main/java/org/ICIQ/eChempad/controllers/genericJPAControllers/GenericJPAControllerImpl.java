/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.genericJPAServices.GenericService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

@Component
@Configuration
public abstract class GenericJPAControllerImpl<T extends JPAEntityImpl, S extends Serializable> implements GenericJPAController<T, S> {

    protected GenericService<T, S> genericService;

    public GenericJPAControllerImpl(GenericService<T, S> genericService) {
        this.genericService = genericService;
    }

    public GenericJPAControllerImpl() { }

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
    public T get(@PathVariable S id) throws ResourceNotExistsException {
        Optional<T> opt = this.genericService.findById(id);
        if (opt.isPresent())
        {
            return opt.get();
        }
        else
        {
            throw new ResourceNotExistsException();
        }
    }

    @PostMapping(
            value = "",
            produces = "application/json",
            consumes = "application/json"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public T add(@Validated @RequestBody T t) {
        Logger.getGlobal().info(t.toString());

        return this.genericService.save(t);
    }


    @PutMapping(
            value = "/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    @PreAuthorize("hasPermission(#id, #t.getType().getCanonicalName(), 'WRITE')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public T put(@Validated @RequestBody T t, @PathVariable(value = "id") S id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        t.setId(id);
        return this.genericService.save(t);
    }


    @DeleteMapping(
            value = "/{id}",
            produces = "application/json"
    )
    @PreAuthorize("hasPermission(#id, @getEntityClass().getCanonicalName(), 'REMOVE')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public T remove(@PathVariable S id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        Optional<T> entity = this.genericService.findById(id);
        if (entity.isPresent()) {
            this.genericService.deleteById(id);
            return entity.get();
        }
        else
            throw new ResourceNotExistsException();
    }
}
