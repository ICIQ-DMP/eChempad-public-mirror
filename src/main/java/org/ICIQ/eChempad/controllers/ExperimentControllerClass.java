/**
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
//@RequestMapping("/api/experiment")
public class ExperimentControllerClass implements ExperimentController{
    private ExperimentService experimentService;

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
        HashSet<Experiment> experiments = new HashSet<>(this.experimentService.getAll());
        return ResponseEntity.ok(experiments);
    }

    // https://stackoverflow.com/questions/30967822/when-do-i-use-path-params-vs-query-params-in-a-restful-api
    // https://restfulapi.net/http-methods/
    @Override
    @GetMapping(
            value = "/api/experiment/{id}",
            produces = "application/json"
    )
    public ResponseEntity<Experiment> getExperiment(@PathVariable(value = "id") UUID uuid) throws ResourceNotExistsException {
        Experiment experiment = this.experimentService.get(uuid);
        return ResponseEntity.ok().body(experiment);
    }


    @PostMapping(
            value = "/api/journal/{journal_uuid}/experiment",
            consumes = "application/json"
    )
    public void addExperimentToJournal(@Validated @RequestBody Experiment experiment, @PathVariable UUID journal_uuid) {
        this.experimentService.addExperimentToJournal(experiment, journal_uuid);
    }

    /**
     * Gets all the experiments belonging to a certain journal.
     *
     * @param journal_uuid UUID of the journal we are querying
     * @return returns all experiments inside the journal if they are readable by the logged user.
     * @throws ResourceNotExistsException  Thrown if the referred journal does not exist in the DB
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to read into this journal.
     */
    @Override
    @GetMapping(
            value = "/api/journal/{journal_uuid}/experiment",
            produces = "application/json"
    )
    public ResponseEntity<Set<Experiment>> getExperimentsFromJournal(@PathVariable UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        return ResponseEntity.ok(this.experimentService.getExperimentsFromJournal(journal_uuid));
    }


    @DeleteMapping(
            value = "/api/experiment/{id}",
            produces = "application/json"
    )
    public void removeExperiment(@PathVariable(value = "id") UUID uuid) throws ResourceNotExistsException {
        this.experimentService.remove(uuid);
    }

    @PutMapping(
            value = "/api/experiment/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    @Override
    public void putExperiment(@Validated @RequestBody Experiment experiment, @PathVariable(value = "id") UUID uuid) throws ResourceNotExistsException {
        this.experimentService.update(experiment, uuid);
    }

}
