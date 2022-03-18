package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.repositories.ExperimentRepository;
import org.ICIQ.eChempad.repositories.GenericRepository;
import org.ICIQ.eChempad.repositories.JournalRepository;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


public interface SecurityService {

    public List<Journal> getAuthorizedJournal(String username, Authority authority);

    List<Journal> getAuthorizedJournal(Authority authority);

}
