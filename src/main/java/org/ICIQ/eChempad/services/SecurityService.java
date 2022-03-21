package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.*;
import org.ICIQ.eChempad.repositories.ExperimentRepository;
import org.ICIQ.eChempad.repositories.GenericRepository;
import org.ICIQ.eChempad.repositories.JournalRepository;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.*;


public interface SecurityService {

    Set<Journal> getAuthorizedJournal(Authority authority);

    Set<Experiment> getAuthorizedExperiment(Authority authority);

    <T extends IEntity> Set<T> getAuthorizedElement(String username, Authority authority, Class<T> type);

    Researcher getLoggedResearcher();

    <T extends IEntity> boolean isLoggedResearcherAuthorizedOverElement(Authority authority, UUID uuid, Class<T> type);
}
