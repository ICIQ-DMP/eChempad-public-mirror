package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.UUID;

public interface ExperimentController {
    ResponseEntity<Set<Experiment>> getExperiments();

    ResponseEntity<Experiment> getExperiment(UUID uuid) throws ResourceNotExistsException;

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

    void removeExperiment(UUID uuid) throws ResourceNotExistsException;

    void putExperiment(Experiment experiment, UUID uuid) throws ResourceNotExistsException;
}
