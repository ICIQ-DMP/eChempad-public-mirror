/**
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


    /**
     * Returns all the Journals readable by the logged user.
     * @return Set of journals
     */
    @Override
    public Set<Journal> getJournals()
    {
        return this.securityService.getAuthorizedJournal(Authority.READ);
    }

    /**
     * Returns the journal identified by the supplied UUID if is READABLE by the logged user.
     * @param journal_uuid UUID of the desired journal
     * @return Journal data
     * @throws ResourceNotExistsException if the desired journal does not exist we throw an exception
     */
    public Journal getJournal(UUID journal_uuid) throws ResourceNotExistsException
    {
        if (this.securityService.isResearcherAuthorized(Authority.READ, journal_uuid, Journal.class))
        {
            return this.genericRepository.get(journal_uuid);
        }
        else
        {
            throw new NotEnoughAuthorityException("You do not have enough authority to read this journal");
        }
    }


    /**
     * Saves the supplied entity of journal. We can always save a journal, but in the workspace of the logged user.
     * @param entity Data parsed coming from the REST API call. Some fields such as the id which are managed with
     *               hibernate could be null
     * @return Returns the same journal that we are adding, but after the transient state is over and all fields are
     *         available.
     */
    public void addJournal(Journal entity)
    {
        this.securityService.saveElementWorkspace(entity);
    }


    /**
     * Updates with the data supplied the journal with the passed UUID
     * @param entity Data that will override the selected journal
     * @param id ID of an existing journal that we want to overwrite
     * @return The same journal that we have updated
     * @throws ResourceNotExistsException This exception is thrown if the supplied id does not coincide with any
     * existing journal.
     */
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
