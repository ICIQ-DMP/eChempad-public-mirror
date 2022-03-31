/**
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.JournalService;
import org.ICIQ.eChempad.services.JournalServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/journal")
public class JournalControllerClass implements JournalController{
    // https://blog.marcnuri.com/inyeccion-de-campos-desaconsejada-field-injection-not-recommended-spring-ioc
    private JournalService journalService;

    @Autowired
    public JournalControllerClass(JournalServiceClass journalServiceClass) {
        this.journalService = journalServiceClass;
    }


    /**
     * Obtains all journals readable from the logged user.
     * @return Collection of journals
     */
    @Override
    @GetMapping(
            value = "",
            produces = "application/json"
    )
    public ResponseEntity<Set<Journal>> getAllJournals() {
        HashSet<Journal> journals = new HashSet<>(this.journalService.getAll());
        return ResponseEntity.ok(journals);
    }


    /**
     * https://stackoverflow.com/questions/30967822/when-do-i-use-path-params-vs-query-params-in-a-restful-api
     * https://restfulapi.net/http-methods/
     * Obtains the journal pointed by the supplied UUID if the logged user has READ rights against the journal.
     * @param uuid UUID of the desired journal
     * @return journal data
     * @throws ResourceNotExistsException Thrown if the supplied UUID does not correspond to any Journal
     */
    @Override
    @GetMapping(
            value = "/{id}",
            produces = "application/json"
    )
    public ResponseEntity<Journal> getJournal(@PathVariable(value = "id") UUID uuid) throws ResourceNotExistsException {
        Journal journal = this.journalService.get(uuid);
        return ResponseEntity.ok().body(journal);
    }


    /**
     * Adds a journal into the user workspace of the logged user.
     * @param journal Journal information from which we will create and add a new journal to our DB
     */
    @PostMapping(
            value = "",
            consumes = "application/json"
    )
    public void addJournal(@Validated @RequestBody Journal journal) {
        this.journalService.save(journal);
    }


    @DeleteMapping(
            value = "/{id}",
            produces = "application/json"
    )
    public void removeJournal(@PathVariable(value = "id") UUID uuid) throws ResourceNotExistsException {
        this.journalService.remove(uuid);
    }

    @PutMapping(
            value = "/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    @Override
    public void putJournal(@Validated @RequestBody Journal journal, @PathVariable(value = "id") UUID uuid) throws ResourceNotExistsException {
        this.journalService.update(journal, uuid);
    }
}
