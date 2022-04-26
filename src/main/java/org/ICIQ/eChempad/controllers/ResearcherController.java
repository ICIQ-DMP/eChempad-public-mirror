/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.UUID;

/**
 * Controller that displays researcher information. The GETs can be called by everyone. The POSTs for addition are
 * special since they are the "sign up" operation. REMOVEs and PUTs can be called by everyone but can only affect the
 * own user, except for the administrator who can call all the methods over all researchers.
 * Researcher in this application is what we generically call the users.
 */
public interface ResearcherController {

    /**
     * Obtains all the researchers available at eChempad application. Researchers information should be public between
     * themselves, since it should be an information that is already present at institutional level.
     * @return Set of all existent researchers.
     */
    ResponseEntity<Set<Researcher>> getResearchers();

    /**
     * Obtains a researcher's information by its UUID. Fails if the researcher does not exist.
     * @param researcher_uuid UUID of the researcher that we want to retrieve.
     * @return Returns information of the supplied researcher.
     * @throws ResourceNotExistsException Thrown if the researcher does not exist.
     */
    ResponseEntity<Researcher> getResearcher(UUID researcher_uuid) throws ResourceNotExistsException;

    /**
     * Adds a new researcher by supplying all the required fields to build a researcher. This operation will be done
     * when we sign up in the application.
     * @param researcher Data of the new researcher.
     */
    void addResearcher(Researcher researcher);

    /**
     * Obtain all data from the Signals Notebook and put it inside the eChempad. It uses the Signals notebook API key
     * stored in the context of the user that is logged in.
     */
    ResponseEntity<String> bulkImportSignals();

    /**
     * Deletes a researcher from the DB, cascading to all of its owned resources, so no data is left behind the user.
     * This operation can be called by all users, but it will only affect the own user, except if we are administrator.
     * @param researcher_uuid UUID of the researcher that we want to delete.
     * @throws ResourceNotExistsException Thrown if the supplied UUID does not point to any researcher.
     * @throws NotEnoughAuthorityException Thrown if a user tries to delete a researcher that is not him.
     */
    void removeResearcher(UUID researcher_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Overwrites the content of an existing researcher with new data.
     * @param researcher Data that will be used to overwrite the current data of the desired researcher.
     * @param researcher_uuid UUID of the researcher that we want to overwrite with new data.
     * @throws ResourceNotExistsException Thrown if the researcher with that UUID does not exist.
     * @throws NotEnoughAuthorityException Thrown if a user tries to update information of a researcher that is not him.
     */
    void putResearcher(Researcher researcher, UUID researcher_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;




}