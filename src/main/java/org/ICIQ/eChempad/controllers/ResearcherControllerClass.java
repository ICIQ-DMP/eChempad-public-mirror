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
import org.ICIQ.eChempad.services.ResearcherServiceClass;
import org.ICIQ.eChempad.services.SecurityService;
import org.ICIQ.eChempad.services.SignalsImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
public class ResearcherControllerClass implements ResearcherController {

    private final ResearcherServiceClass researcherServiceClass;

    private final SignalsImportService signalsImportService;

    private final SecurityService securityService;

    @Autowired
    public ResearcherControllerClass(ResearcherServiceClass researcherServiceClass, SignalsImportService signalsImportService, SecurityService securityService) {
        this.signalsImportService = signalsImportService;
        this.researcherServiceClass = researcherServiceClass;
        this.securityService = securityService;
    }

    @Override
    @GetMapping(
            value = "/api/researcher",
            produces = "application/json"
    )
    public ResponseEntity<Set<Researcher>> getResearchers() {
        HashSet<Researcher> researchers = new HashSet<>(this.researcherServiceClass.get());
        return ResponseEntity.ok(researchers);
    }

    @Override
    @GetMapping(
            value = "/api/researcher/{researcher_uuid}",
            produces = "application/json"
    )
    public ResponseEntity<Researcher> getResearcher(@PathVariable UUID researcher_uuid) throws ResourceNotExistsException {
        Researcher researcher = this.researcherServiceClass.get(researcher_uuid);
        return ResponseEntity.ok().body(researcher);
    }

    @Override
    @PostMapping(
            value = "/api/researcher",
            consumes = "application/json"
    )
    public void addResearcher(@Validated @RequestBody Researcher researcher) {
        this.researcherServiceClass.save(researcher);
    }

    @Override
    @GetMapping(value = "/api/researcher/signals")
    public ResponseEntity<String> bulkImportSignals() {
        try {
            return ResponseEntity.ok().body(this.signalsImportService.importSignals(this.securityService.getLoggedResearcher().getSignalsAPIKey()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("Data from this Signals account could not have been imported.");
    }

    @Override
    @DeleteMapping(
            value = "/api/researcher/{researcher_uuid}",
            produces = "application/json"
    )
    public void removeResearcher(@PathVariable UUID researcher_uuid) throws ResourceNotExistsException {
        this.researcherServiceClass.remove(researcher_uuid);
    }

    @Override
    @PutMapping(
            value = "/api/researcher/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    public void putResearcher(@Validated @RequestBody Researcher researcher, @PathVariable(value = "id") UUID researcher_uuid) throws ResourceNotExistsException {
        this.researcherServiceClass.update(researcher, researcher_uuid);
    }



}
