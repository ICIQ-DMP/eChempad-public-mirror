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
import org.ICIQ.eChempad.services.ResearcherServiceClass;
import org.ICIQ.eChempad.services.SignalsImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Controller that displays researcher information. The GETs can be called by everyone. The POSTs for addition are
 * special since they are the "sign up" operation. REMOVEs and PUTs can be called by everyone but can only affect the
 * own user, except for the administrator who can call all the methods over all researchers.
 * Researcher in this application is what we generically call the users.
 */
@RestController
public class ResearcherControllerClass implements ResearcherController {

    private final ResearcherServiceClass researcherServiceClass;

    private final SignalsImportService signalsImportService;

    @Autowired
    public ResearcherControllerClass(ResearcherServiceClass researcherServiceClass, SignalsImportService signalsImportService) {
        this.signalsImportService = signalsImportService;
        this.researcherServiceClass = researcherServiceClass;
    }


    /**
     * Obtains all the researchers available at eChempad application. Researchers information should be public between
     * themselves, since it should be an information that is already present at institutional level.
     * @return Set of all existent researchers.
     */
    @Override
    @GetMapping(
            value = "/api/researcher",
            produces = "application/json"
    )
    public ResponseEntity<Set<Researcher>> getResearchers() {
        HashSet<Researcher> researchers = new HashSet<>(this.researcherServiceClass.get());
        return ResponseEntity.ok(researchers);
    }


    /**
     * Obtains a researcher's information by its UUID. Fails if the researcher does not exist.
     * @param researcher_uuid UUID of the researcher that we want to retrieve.
     * @return Returns information of the supplied researcher.
     * @throws ResourceNotExistsException Thrown if the researcher does not exist.
     */
    @Override
    @GetMapping(
            value = "/api/researcher/{researcher_uuid}",
            produces = "application/json"
    )
    public ResponseEntity<Researcher> getResearcher(@PathVariable UUID researcher_uuid) throws ResourceNotExistsException {
        Researcher researcher = this.researcherServiceClass.get(researcher_uuid);
        return ResponseEntity.ok().body(researcher);
    }


    /**
     * Adds a new researcher by supplying all the required fields to build a researcher. This operation will be done
     * when we sign up in the application.
     * @param researcher Data of the new researcher.
     */
    @PostMapping(
            value = "/api/researcher",
            consumes = "application/json"
    )
    public void addResearcher(@Validated @RequestBody Researcher researcher) {
        this.researcherServiceClass.save(researcher);
    }

    @GetMapping(value = "/api/researcher/signals")
    @Override
    public void bulkImportSignals() {
        try {
            this.signalsImportService.importSignals();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }















    /**
     * Deletes a researcher from the DB, cascading to all of its owned resources, so no data is left behind the user.
     * This operation can be called by all users, but it will only affect the own user, except if we are administrator.
     * @param researcher_uuid UUID of the researcher that we want to delete.
     * @throws ResourceNotExistsException Thrown if the supplied UUID does not point to any researcher.
     * @throws NotEnoughAuthorityException Thrown if a user tries to delete a researcher that is not him.
     */
    @DeleteMapping(
            value = "/api/researcher/{researcher_uuid}",
            produces = "application/json"
    )
    public void removeResearcher(@PathVariable UUID researcher_uuid) throws ResourceNotExistsException {
        this.researcherServiceClass.remove(researcher_uuid);
    }


    /**
     * Overwrites the content of an existing researcher with new data.
     * @param researcher Data that will be used to overwrite the current data of the desired researcher.
     * @param researcher_uuid UUID of the researcher that we want to overwrite with new data.
     * @throws ResourceNotExistsException Thrown if the researcher with that UUID does not exist.
     * @throws NotEnoughAuthorityException Thrown if a user tries to update information of a researcher that is not him.
     */
    @PutMapping(
            value = "/api/researcher/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    @Override
    public void putResearcher(@Validated @RequestBody Researcher researcher, @PathVariable(value = "id") UUID researcher_uuid) throws ResourceNotExistsException {
        this.researcherServiceClass.update(researcher, researcher_uuid);
    }



}
