/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.repositories.ExperimentRepository;
import org.ICIQ.eChempad.repositories.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class ExperimentServiceClass extends GenericServiceClass<Experiment, UUID> implements ExperimentService {

    final SecurityService securityService;
    final JournalRepository journalRepository;

    @Autowired
    public ExperimentServiceClass(ExperimentRepository experimentRepository, SecurityService securityService, JournalRepository journalRepository) {
        super(experimentRepository);
        this.securityService = securityService;
        this.journalRepository = journalRepository;
    }

    @Override
    public void addExperimentToJournal(Experiment experiment, UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        // TODO query journal when we know that we have permissions.
        Journal journal = this.journalRepository.get(journal_uuid);

        // Check permissions
        if (this.securityService.isResearcherAuthorized(Authority.WRITE, journal_uuid, Journal.class))
        {
            // experiment points to the journal where it is in
            experiment.setJournal(journal);
            // Save the experiment
            this.genericRepository.saveOrUpdate(experiment);
            // Then add to experiment the new doc
            journal.getExperiments().add(experiment);
            // And save (update) experiment
            this.journalRepository.saveOrUpdate(journal);
            this.securityService.saveElementWorkspace(experiment);
        }
        else
        {
            throw new NotEnoughAuthorityException("Not enough authority to perform operation");
        }
    }

    @Override
    public Set<Experiment> getExperimentsFromJournal(UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        Journal journal = this.journalRepository.get(journal_uuid);
        if (this.securityService.isResearcherAuthorized(Authority.READ, journal.getUUid(), Journal.class))
        {
            return journal.getExperiments();
        }
        else
        {
            throw new NotEnoughAuthorityException("You do not have authority to read this journal");
        }
    }

    @Override
    public Set<Experiment> getExperiments() {
        return this.securityService.getAuthorizedExperiment(Authority.READ);
    }

    @Override
    public Experiment getExperiment(UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        Experiment experiment = this.genericRepository.get(experiment_uuid);
        if (this.securityService.isResearcherAuthorized(Authority.READ, experiment_uuid, Experiment.class))
        {
            return experiment;
        }
        else
        {
            throw new NotEnoughAuthorityException("You do not have authority to read this journal");
        }
    }

}
