/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.repositories.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class JournalServiceClass extends GenericServiceClass<Journal, UUID> implements JournalService {

    final SecurityService securityService;

    /**
     * Creates a new Journal service using the SecurityService and a JournalRepository
     * @param journalRepository Regulates access from Java code to the Table of Journals.
     * @param securityService Contains auxiliar functions to verify and apply authentication and authorization
     */
    @Autowired
    public JournalServiceClass(JournalRepository journalRepository, SecurityService securityService) {
        super(journalRepository);
        this.securityService = securityService;
    }

    @Override
    public Set<Journal> getJournals()
    {
        return this.securityService.getAuthorizedJournal(Authority.READ);
    }

    @Override
    public Journal getJournal(UUID journal_uuid) throws ResourceNotExistsException
    {
        Optional<Journal> option = this.securityService.getAuthorizedJournal(Authority.READ).stream().filter(journal -> journal.getUUid().equals(journal_uuid)).findFirst();
        if (option.isPresent())
        {
            return option.get();
        }
        else
        {
            throw new NotEnoughAuthorityException("You do not have enough authority to read this journal");
        }
    }

    @Override
    public void addJournal(Journal entity)
    {
        this.securityService.saveElementWorkspace(entity);
    }


    public Journal update(Journal entity, UUID id) throws ResourceNotExistsException
    {
        return (Journal) this.securityService.updateElement(entity, id);
    }

    public void remove(UUID id) throws ResourceNotExistsException
    {
        if (this.securityService.isResearcherAuthorized(Authority.EDIT, id, Journal.class))
        {
            super.remove(id);
        }
        else
        {
            // TODO: error
            // no-op
        }
    }

}
