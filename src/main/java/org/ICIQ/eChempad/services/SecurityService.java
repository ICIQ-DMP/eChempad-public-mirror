package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.*;

import java.util.*;


public interface SecurityService {

    Set<Journal> getAuthorizedJournal(Authority authority);

    Set<Experiment> getAuthorizedExperiment(Authority authority);

    <T extends IEntity> Set<T> getAuthorizedElement(String username, Authority authority, Class<T> type);

    Researcher getLoggedResearcher();

    <T extends IEntity> boolean isResearcherAuthorized(Authority authority, UUID uuid, Class<T> type);
}
