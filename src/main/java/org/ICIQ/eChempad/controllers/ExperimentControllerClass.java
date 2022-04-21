/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.ExperimentService;
import org.ICIQ.eChempad.services.ExperimentServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
public class ExperimentControllerClass implements ExperimentController {
    private final ExperimentService experimentService;

    @Autowired
    public ExperimentControllerClass(ExperimentServiceClass experimentServiceClass) {
        this.experimentService = experimentServiceClass;
    }

    @Override
    @GetMapping(
            value = "/api/experiment",
            produces = "application/json"
    )
    public ResponseEntity<Set<Experiment>> getExperiments() {
        HashSet<Experiment> experiments = new HashSet<>(this.experimentService.getExperiments());
        return ResponseEntity.ok(experiments);
    }

    @Override
    @GetMapping(
            value = "/api/experiment/{id}",
            produces = "application/json"
    )
    public ResponseEntity<Experiment> getExperiment(@PathVariable(value = "id") UUID experiment_uuid) throws ResourceNotExistsException {
        Experiment experiment = this.experimentService.getExperiment(experiment_uuid);
        return ResponseEntity.ok().body(experiment);
    }

    @Override
    @PostMapping(
            value = "/api/journal/{journal_uuid}/experiment",
            consumes = "application/json"
    )
    public void addExperimentToJournal(@Validated @RequestBody Experiment experiment, @PathVariable UUID journal_uuid) {
        this.experimentService.addExperimentToJournal(experiment, journal_uuid);
    }

    @Override
    @GetMapping(
            value = "/api/journal/{journal_uuid}/experiment",
            produces = "application/json"
    )
    public ResponseEntity<Set<Experiment>> getExperimentsFromJournal(@PathVariable UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        return ResponseEntity.ok(this.experimentService.getExperimentsFromJournal(journal_uuid));
    }

    @Override
    @DeleteMapping(
            value = "/api/experiment/{id}",
            produces = "application/json"
    )
    public void removeExperiment(@PathVariable(value = "id") UUID experiment_uuid) throws ResourceNotExistsException {
        this.experimentService.remove(experiment_uuid);
    }

    @Override
    @PutMapping(
            value = "/api/experiment/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    public void putExperiment(@Validated @RequestBody Experiment experiment, @PathVariable(value = "id") UUID experiment_uuid) throws ResourceNotExistsException {
        this.experimentService.update(experiment, experiment_uuid);
    }

}
