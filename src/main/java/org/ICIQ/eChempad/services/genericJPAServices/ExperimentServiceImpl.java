/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.configurations.security.ACL.AclServiceCustomImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Experiment;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.repositories.genericJPARepositories.ExperimentRepository;
import org.ICIQ.eChempad.repositories.genericJPARepositories.JournalRepository;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class ExperimentServiceImpl<T extends JPAEntityImpl, S extends Serializable> extends GenericServiceImpl<Experiment, UUID> implements ExperimentService<Experiment, UUID> {

    @Autowired
    private JournalService<Journal, UUID> journalService;

    @Autowired
    public ExperimentServiceImpl(ExperimentRepository<T, S> experimentRepository, AclServiceCustomImpl aclRepository) {
        super(experimentRepository, aclRepository);
    }


    /**
     * Adds a new experiment into a certain journal, provided as a URL parameter.
     *
     * @param experiment   Data of the experiment we want to add to the journal.
     * @param journal_uuid Journal where we are adding the new experiment. We must have writing permissions.
     * @throws ResourceNotExistsException  The resource does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have the writing authority to perform the operation
     *                                     in the journal.
     */
    @Override
    public Experiment addExperimentToJournal(Experiment experiment, UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {

        Logger.getGlobal().warning("Adding experiment " + experiment.getName());
        // Sometimes I get this error "illegally attempted to associate a proxy with two open Sessions". We can
        // solve it by using the actual repository, but having less performance since we do a DB connection
        Journal journal = this.journalService.getById(journal_uuid);

        // Set the journal of this experiment and sav experiment. Save is cascaded
        experiment.setJournal(journal);
        Experiment experimentDB = this.genericRepository.save(experiment);

        // Add all permissions to experiment for the current user, and set also inheriting entries from parent journal
        this.aclRepository.addAllPermissionToLoggedUserInEntity(experimentDB, true, journal, Journal.class);

        return experimentDB;
    }

    /**
     * Gets the experiments of a certain journal if we have enough privileges to read them.
     *
     * @param journal_uuid UUID of the journal we are retrieving experiments from.
     * @return Returns all the experiments under this journal if they can be read.
     * @throws ResourceNotExistsException  Thrown if this journal does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to read the journal.
     */
    @Override
    public Set<Experiment> getExperimentsFromJournal(UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        // We punctually use the Entity manager to get a Journal entity from the Experiment service
        return super.entityManager.find(Journal.class, journal_uuid).getExperiments();
    }
}
