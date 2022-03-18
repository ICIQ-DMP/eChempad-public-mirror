package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.GenericRepository;
import org.ICIQ.eChempad.repositories.JournalRepository;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class JournalServiceClass extends GenericServiceClass<Journal, UUID> implements JournalService {

    @Autowired
    SecurityServiceImpl securityServiceImpl;

    @Autowired
    public JournalServiceClass(JournalRepository journalRepository) {
        super(journalRepository);
    }

    public List<Journal> getAllReadable()
    {
        return securityServiceImpl.getAuthorizedJournal(Authority.READ);
    }
}
