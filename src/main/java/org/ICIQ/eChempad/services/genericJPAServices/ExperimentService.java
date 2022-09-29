/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Experiment;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public interface ExperimentService<T extends JPAEntityImpl, S extends Serializable> extends GenericService<Experiment, UUID> {

    /**
     * Adds a new experiment into a certain journal, provided as a URL parameter.
     * @param experiment Data of the experiment we want to add to the journal.
     * @param journal_uuid Journal where we are adding the new experiment. We must have writing permissions.
     * @throws ResourceNotExistsException The resource does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have the writing authority to perform the operation
     *                                     in the journal.
     */
    Experiment addExperimentToJournal(Experiment experiment, UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Gets the experiments of a certain journal if we have enough privileges to read them.
     * @param journal_uuid UUID of the journal we are retrieving experiments from.
     * @return Returns all the experiments under this journal if they can be read.
     * @throws ResourceNotExistsException Thrown if this journal does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to read the journal.
     */
    Set<Experiment> getExperimentsFromJournal(UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

}
