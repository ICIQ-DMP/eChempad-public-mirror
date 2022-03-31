/**
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.UUID;

/**
 * GET /researcher :
 *   Obtains all researchers.
 * GET /researcher/:id :
 *   Obtains a certain researcher instance via GET on the specific endpoint of that certain researcher, which is
 *   composed by its uuid.
 * POST /researcher :
 *   Adds a new researcher instance by submitting the required fields of the researcher.
 * PUT /researcher/:id :
 *   Modifies the data of a researcher by supplying the fields that will be modified. Return error if UUID not found.
 * DELETE /researcher/:id
 *   Deletes a researcher from the database from its id. Return error if UUID not found.
 */
public interface ResearcherController {
    ResponseEntity<Set<Researcher>> getResearchers();

    ResponseEntity<Researcher> getResearcher(UUID uuid) throws ResourceNotExistsException;

    void addResearcher(Researcher researcher);

    void removeResearcher(UUID uuid) throws ResourceNotExistsException;

    void putResearcher(Researcher researcher, UUID uuid) throws ResourceNotExistsException;
}