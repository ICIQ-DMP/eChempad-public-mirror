/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.ResearcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
public class ResearcherControllerImpl implements ResearcherController {

    private final ResearcherService<Researcher, UUID> researcherService;

    @Autowired
    public ResearcherControllerImpl(ResearcherService<Researcher, UUID> researcherService) {
        this.researcherService = researcherService;
    }

    @Override
    @GetMapping(
            value = "/api/researcher",
            produces = "application/json"
    )
    public ResponseEntity<Set<Researcher>> getResearchers() {
        HashSet<Researcher> researchers = new HashSet<>(this.researcherService.findAll());
        return ResponseEntity.ok(researchers);
    }

    @Override
    @GetMapping(
            value = "/api/researcher/{researcher_uuid}",
            produces = "application/json"
    )
    public ResponseEntity<Researcher> getResearcher(@PathVariable UUID researcher_uuid) throws ResourceNotExistsException {
        Optional<Researcher> opt = this.researcherService.findById(researcher_uuid);
        if (opt.isPresent())
        {
            return ResponseEntity.ok().body(opt.get());
        }
        else
        {
            throw new ResourceNotExistsException();
        }
    }

    @Override
    @PostMapping(
            value = "/api/researcher",
            consumes = "application/json"
    )
    public void addResearcher(@Validated @RequestBody Researcher researcher) {
        this.researcherService.save(researcher);
    }

    @Override
    @GetMapping(value = "/api/researcher/signals")
    public ResponseEntity<String> bulkImportSignals() {
        return ResponseEntity.ok().body("Data from this Signals account could not have been imported.");
    }

    @Override
    @DeleteMapping(
            value = "/api/researcher/{researcher_uuid}",
            produces = "application/json"
    )
    public void removeResearcher(@PathVariable UUID researcher_uuid) throws ResourceNotExistsException {
        this.researcherService.deleteById(researcher_uuid);
    }

    @Override
    @PutMapping(
            value = "/api/researcher/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    public void putResearcher(@Validated @RequestBody Researcher researcher, @PathVariable(value = "id") UUID researcher_uuid) throws ResourceNotExistsException {
        researcher.setUUid(researcher_uuid);
        this.researcherService.save(researcher);
    }
}
