package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.GenericRepository;
import org.ICIQ.eChempad.repositories.JournalRepository;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JournalServiceClass extends GenericServiceClass<Journal, UUID> implements JournalService {

    @Autowired
    public JournalServiceClass(JournalRepository journalRepository) {
        super(journalRepository);
    }
}
