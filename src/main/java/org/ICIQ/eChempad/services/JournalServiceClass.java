package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.exceptions.ExceptionResourceNotExists;
import org.ICIQ.eChempad.repositories.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class JournalServiceClass extends GenericServiceClass<Journal, UUID> implements JournalService {

    final
    SecurityService securityService;

    @Autowired
    public JournalServiceClass(JournalRepository journalRepository, SecurityService securityService) {
        super(journalRepository);
        this.securityService = securityService;
    }

    @Override
    public Set<Journal> getAll()
    {
        return securityService.getAuthorizedJournal(Authority.READ);
    }

    public Journal saveOrUpdate(Journal entity)
    {
        if (this.securityService.isLoggedResearcherAuthorizedOverElement(Authority.WRITE, entity.getUUid(), entity.getMyType()))
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
        if (this.securityService.isLoggedResearcherAuthorizedOverElement(Authority.WRITE, id, entity.getMyType()))
        {
            return super.update(entity, id);
        }
        else
        {
            // TODO: error
            return null;
        }
    }


    public Journal get(UUID id) throws ExceptionResourceNotExists
    {
        if (this.securityService.isLoggedResearcherAuthorizedOverElement(Authority.WRITE, id, Journal.class))
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
        if (this.securityService.isLoggedResearcherAuthorizedOverElement(Authority.EDIT, id, Journal.class))
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
