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
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.UUID;

public interface ExperimentController {

    /**
     * Obtain all experiments accessible by the logged user.
     * @return Set of Readable experiments by the logged user.
     */
    ResponseEntity<Set<Experiment>> getExperiments();

    /**
     * Gets a certain experiment if we have enough permissions to read it and the resource exists
     * @param experiment_uuid UUID of the accessed experiment.
     * @return Returns the experiment wrapped in an HTTP response.
     * @throws ResourceNotExistsException Thrown if the received UUID does not correspond to any resource.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to read the experiment we sent.
     */
    ResponseEntity<Experiment> getExperiment(UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Adds an experiment to a certain journal if we have enough permissions
     * @param experiment data of the new experiment.
     * @param journal_uuid Id of the journal we are adding.
     * @throws ResourceNotExistsException Thrown if the referred journal does not exist in the DB
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to write into this journal.
     */
    void addExperimentToJournal(Experiment experiment, UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Gets all the experiments belonging to a certain journal.
     * @param journal_uuid UUID of the journal we are querying
     * @return returns all experiments inside the journal if they are readable by the logged user.
     * @throws ResourceNotExistsException Thrown if the referred journal does not exist in the DB
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to read into this journal.
     */
    ResponseEntity<Set<Experiment>> getExperimentsFromJournal(UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;














    /**
     * Removes the experiment selected by its UUID if the logged user have enough permissions to do so.
     * @param experiment_uuid UUID of the experiment that we are removing.
     * @throws ResourceNotExistsException Thrown if the resource does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have EDIT permissions against the removed experiment.
     */
    void removeExperiment(UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Updates the experiment with the corresponding UUID if the logged user has enough permissions to do so.
     * @param experiment Experiment data that we want to put in place of the older data of the experiment.
     * @param experiment_uuid UUID of the experiment that we want to update. It has to exist.
     * @throws ResourceNotExistsException Thrown if the resource with this UUID does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough permissions to update the desired experiment.
     */
    void putExperiment(Experiment experiment, UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;
}
