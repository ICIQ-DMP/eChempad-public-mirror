/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.GenericJPAEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.UUID;

/**
 * Controller that displays researcher information. The GETs can be called by everyone. The POSTs for addition are
 * special since they are the "sign up" operation. REMOVEs and PUTs can be called by everyone but can only affect the
 * own user, except for the administrator who can call all the methods over all researchers.
 * Researcher in this application is what we generically call the users.
 */
public interface ResearcherController<T extends GenericJPAEntity, S extends Serializable> extends GenericController<Researcher, UUID> {

    /**
     * Obtain all data from the Signals Notebook and put it inside the eChempad. It uses the Signals notebook API key
     * stored in the context of the user that is logged in.
     */
    ResponseEntity<String> bulkImportSignals();



}