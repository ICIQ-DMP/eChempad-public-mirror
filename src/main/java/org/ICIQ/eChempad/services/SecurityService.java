/*
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

    /**
     * Return the Researcher that is logged in using the information in the SecurityContextHolder
     * @return Instance of the logged researcher.
     */
    Researcher getLoggedResearcher();

    /**
     * Inspects the elementpermission table / class to search if there is a certain element of a certain desired type
     * that has a granted authority to the logged user bigger than the required one or not. Returns true if yes,
     * returns false if not.
     * @param authority Required level of authority.
     * @param uuid ID of an element
     * @param type Type of our element
     * @param <T> Parameter IEntity that conforms to the implementation of all of our elements.
     * @return True or false depending on if we can do the required operation on the element or not.
     */
    <T extends IEntity> boolean isResearcherAuthorized(Authority authority, UUID uuid, Class<T> type);

    /**
     * Saves a received generic element to the workspace of the logged user.
     * @param element Generic entity
     * @return Generic entity with the data fields managed by springboot in.
     */
    IEntity saveElementWorkspace(IEntity element);

    /**
     * Updates the element pointed by the supplied UUID with the data supplied via the IEntity element.
     * @param element contains data used to override the existing
     * @param uuid    Points to a previously existing resource.
     * @return Returns the same entity we updated, now fully managed by spring boot.
     */
    IEntity updateElement(IEntity element, UUID uuid);

    /**
     * Generic method that returns all the IEntity objects of certain type T from a researcher identified by its
     * username (email) with a greater authority than the requested: If we ask for read authority we will get all the
     * journals except the ones with NONE authority.
     * @param username email that identifies a researcher
     * @param authority Selected level of permissions of the elements we retrieve
     * @param type Explicit type of the elements we are retrieving. Can be Experiment or Journal.
     * @param <T> Generic type that extends an IEntity
     * @return List of elements that conform to the supplied desired characteristics.
     */
    <T extends IEntity> Set<T> getAuthorizedElement(String username, Authority authority, Class<T> type);

    /**
     * Get all the Journals that have an authorization level below or equal to the authorization required for the
     * logged user.
     * @param authority Level of authorization required
     * @return All the journal where the current user has more privileges assigned than the required ones.
     */
    Set<Journal> getAuthorizedJournal(Authority authority);

    /**
     * Get all experiments that have an authorization level below or equal to the authorization required for the
     * logged user.
     * @param authority Level of authorization required.
     * @return All the experiment where the current user has more privileges assigned than the required ones.
     */
    Set<Experiment> getAuthorizedExperiment(Authority authority);

    /**
     * Get all documents that have an authorization level below or equal to the authorization required for the
     * logged user.
     * @param authority Level of authorization required.
     * @return All the documents where the current user has more privileges assigned than the required ones.
     */
    Set<Document> getAuthorizedDocument(Authority authority);







}
