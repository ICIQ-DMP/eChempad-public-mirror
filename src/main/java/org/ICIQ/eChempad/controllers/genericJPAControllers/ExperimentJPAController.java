/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.Experiment;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public interface ExperimentJPAController<T extends JPAEntityImpl, S extends Serializable> extends GenericJPAController<Experiment, UUID> {

    /**
     * Adds an experiment to a certain journal if we have enough permissions.
     * @param experiment data of the new experiment.
     * @param journal_uuid UUID of the journal we are adding.
     * @throws ResourceNotExistsException Thrown if the referred journal does not exist in the DB
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to write into this journal.
     */
    Experiment addExperimentToJournal(Experiment experiment, UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Gets all the experiments belonging to a certain journal.
     *
     * @param journal_uuid UUID of the journal we are querying
     * @return returns all experiments inside the journal if they are readable by the logged user.
     * @throws ResourceNotExistsException  Thrown if the referred journal does not exist in the DB
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to read into this journal.
     */
    Set<Experiment> getExperimentsFromJournal(UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

}
