/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.GenericServiceImpl;
import org.ICIQ.eChempad.services.ResearcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/researcher")
public class ResearcherControllerImpl<T extends IEntity, S extends Serializable> extends GenericControllerImpl<Researcher, UUID> implements ResearcherController<Researcher, UUID>  {

    @Autowired
    public ResearcherControllerImpl(ResearcherService<Researcher, UUID> researcherService) {
        super(researcherService);
    }

    @Override
    @GetMapping(value = "/api/researcher/signals")
    public ResponseEntity<String> bulkImportSignals() {
        return ResponseEntity.ok().body("Data from this Signals account could not have been imported.");
    }

}
