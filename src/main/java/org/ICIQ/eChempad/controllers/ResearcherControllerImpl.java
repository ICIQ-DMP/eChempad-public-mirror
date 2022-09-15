/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.GenericServiceImpl;
import org.ICIQ.eChempad.services.ResearcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/researcher")
public class ResearcherControllerImpl<T extends IEntity, S extends Serializable> extends GenericControllerImpl<Researcher, UUID> implements ResearcherController<Researcher, UUID>  {

    @Autowired
    public ResearcherControllerImpl(ResearcherService<Researcher, UUID> researcherService) {
        super(researcherService);
    }

    @Override
    @GetMapping(value = "/api/researcher/signals")
    public ResponseEntity<String> bulkImportSignals() {
        return ResponseEntity.ok().body("Data from this Signals account could not have been imported.");
    }

    /**
     * The second arg of hasPermissions is what forbids with my current knowledge the existence of a generic remove
     * method, since jackson needs to know the type to be able to serialize / deserialize objects.
     * @param id Identifier of the researcher
     * @return Deleted researcher
     * @throws ResourceNotExistsException If the resource does not exist.
     * @throws NotEnoughAuthorityException If we cannot delete the resource because of permissions.
     */
    @DeleteMapping(
            value = "/{id}",
            produces = "application/json"
    )
    @PreAuthorize("hasPermission(#id, 'org.ICIQ.eChempad.entities.Researcher' , 'DELETE')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public Researcher remove(@PathVariable UUID id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        Optional<Researcher> entity = this.genericService.findById(id);
        if (entity.isPresent()) {
            this.genericService.deleteById(id);
            return entity.get();
        }
        else
            throw new ResourceNotExistsException();
    }

}
