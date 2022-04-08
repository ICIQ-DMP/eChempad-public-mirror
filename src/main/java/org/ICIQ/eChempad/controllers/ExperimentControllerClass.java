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
public class ExperimentControllerClass implements ExperimentController {
    private final ExperimentService experimentService;

    @Autowired
    public ExperimentControllerClass(ExperimentServiceClass experimentServiceClass) {
        this.experimentService = experimentServiceClass;
    }


    /**
     * Obtain all experiments accessible by the logged user.
     * @return Set of Readable experiments by the logged user.
     */
    @Override
    @GetMapping(
            value = "/api/experiment",
            produces = "application/json"
    )
    public ResponseEntity<Set<Experiment>> getExperiments() {
        HashSet<Experiment> experiments = new HashSet<>(this.experimentService.getExperiments());
        return ResponseEntity.ok(experiments);
    }

    /**
     * Gets a certain experiment if we have enough permissions to read it and the resource exists.
     * @param experiment_uuid UUID of the accessed experiment.
     * @return Returns the experiment wrapped in an HTTP response.
     * @throws ResourceNotExistsException Thrown if the received UUID does not correspond to any resource.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to read the experiment we sent.
     */
    @Override
    @GetMapping(
            value = "/api/experiment/{id}",
            produces = "application/json"
    )
    public ResponseEntity<Experiment> getExperiment(@PathVariable(value = "id") UUID experiment_uuid) throws ResourceNotExistsException {
        Experiment experiment = this.experimentService.getExperiment(experiment_uuid);
        return ResponseEntity.ok().body(experiment);
    }

    /**
     * Adds an experiment to a certain journal if we have enough permissions
     * @param experiment data of the new experiment.
     * @param journal_uuid UUID of the journal we are adding.
     * @throws ResourceNotExistsException Thrown if the referred journal does not exist in the DB
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to write into this journal.
     */
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




























    /**
     * Removes the experiment selected by its UUID if the logged user have enough permissions to do so.
     * @param experiment_uuid UUID of the experiment that we are removing.
     * @throws ResourceNotExistsException Thrown if the resource does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have EDIT permissions against the removed experiment.
     */
    @DeleteMapping(
            value = "/api/experiment/{id}",
            produces = "application/json"
    )
    public void removeExperiment(@PathVariable(value = "id") UUID experiment_uuid) throws ResourceNotExistsException {
        this.experimentService.remove(experiment_uuid);
    }


    /**
     * Updates the experiment with the corresponding UUID if the logged user has enough permissions to do so.
     * @param experiment Experiment data that we want to put in place of the older data of the experiment.
     * @param experiment_uuid UUID of the experiment that we want to update. It has to exist.
     * @throws ResourceNotExistsException Thrown if the resource with this UUID does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough permissions to update the desired experiment.
     */
    @PutMapping(
            value = "/api/experiment/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    @Override
    public void putExperiment(@Validated @RequestBody Experiment experiment, @PathVariable(value = "id") UUID experiment_uuid) throws ResourceNotExistsException {
        this.experimentService.update(experiment, experiment_uuid);
    }

}
