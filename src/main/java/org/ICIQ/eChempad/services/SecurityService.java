/**
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.*;

import java.util.*;


public interface SecurityService {

    Set<Journal> getAuthorizedJournal(Authority authority);

    Set<Experiment> getAuthorizedExperiment(Authority authority);

    Set<Document> getAuthorizedDocument(Authority authority);

    <T extends IEntity> Set<T> getAuthorizedElement(String username, Authority authority, Class<T> type);

    Researcher getLoggedResearcher();

    <T extends IEntity> boolean isResearcherAuthorized(Authority authority, UUID uuid, Class<T> type);

    IEntity saveElementWorkspace(IEntity element);

    /**
     * Updates the element pointed by the supplied UUID with the data supplied via the IEntity element.
     * @param element contains data used to override the existing
     * @param uuid Points to a previously existing resource.
     * @return Returns the same entity we updated, now fully managed by spring boot.
     */
    IEntity updateElement(IEntity element, UUID uuid);
}
