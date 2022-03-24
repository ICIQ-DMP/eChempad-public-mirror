package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.exceptions.ExceptionResourceNotExists;
import org.ICIQ.eChempad.repositories.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class JournalServiceClass extends GenericServiceClass<Journal, UUID> implements JournalService {

    final
    SecurityService securityService;

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
    public Set<Journal> getAll()
    {
        Set<Journal> journals = securityService.getAuthorizedJournal(Authority.READ);
        return journals;
    }


    /**
     * Save or update the supplied entity of journal.
     * @param entity
     * @return
     */
    public Journal saveOrUpdate(Journal entity)
    {
        if (this.securityService.isResearcherAuthorized(Authority.WRITE, entity.getUUid(), entity.getMyType()))
        {
            return super.saveOrUpdate(entity);
        }
        else
        {
            // TODO: error
            return null;
        }
    }

    public Journal update(Journal entity, UUID id) throws ExceptionResourceNotExists
    {
        if (this.securityService.isResearcherAuthorized(Authority.WRITE, id, entity.getMyType()))
        {
            return super.update(entity, id);
        }
        else
        {
            // TODO: error
            return null;
        }
    }

    /**
     * Returns the journal identified by the supplied UUID if is READABLE by the logged user.
     * @param id UUID of the desired journal
     * @return Journal data
     * @throws ExceptionResourceNotExists if the desired journal does not exist we throw an exception
     */
    public Journal get(UUID id) throws ExceptionResourceNotExists
    {
        if (this.securityService.isResearcherAuthorized(Authority.READ, id, Journal.class))
        {
            return super.get(id);
        }
        else
        {
            // TODO: error
            return null;
        }
    }


    public void remove(UUID id) throws ExceptionResourceNotExists
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
