/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.JournalService;
import org.ICIQ.eChempad.services.JournalServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
public class JournalControllerClass implements JournalController{
    private final JournalService journalService;

    @Autowired
    public JournalControllerClass(JournalServiceClass journalServiceClass) {
        this.journalService = journalServiceClass;
    }

    @Override
    @GetMapping(
            value = "/api/journal",
            produces = "application/json"
    )
    public ResponseEntity<Set<Journal>> getJournals() {
        ResponseEntity<Set<Journal>> res = ResponseEntity.ok(this.journalService.getJournals());
        return res;
    }

    @Override
    @GetMapping(
            value = "/api/journal/{journal_uuid}",
            produces = "application/json"
    )
    public ResponseEntity<Journal> getJournal(@PathVariable UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        return ResponseEntity.ok().body(this.journalService.get(journal_uuid));
    }

    @Override
    @PostMapping(
            value = "/api/journal",
            consumes = "application/json"
    )
    public void addJournal(@Validated @RequestBody Journal journal) {
        this.journalService.save(journal);
    }

    @Override
    @GetMapping(
            value = "/api/researcher/{researcher_uuid}/journal",
            consumes = "application/json"
    )
    public ResponseEntity<Set<Journal>> getJournalsFromResearcher(@PathVariable UUID researcher_uuid) throws ResourceNotExistsException {
        return null;
        // TODO
    }

    @Override
    @DeleteMapping(
            value = "/api/journal/{journal_uuid}",
            produces = "application/json"
    )
    public void removeJournal(@PathVariable UUID journal_uuid) throws ResourceNotExistsException {
        this.journalService.remove(journal_uuid);
    }

    @Override
    @PutMapping(
            value = "/api/journal/{journal_uuid}",
            produces = "application/json",
            consumes = "application/json"
    )
    public void putJournal(@Validated @RequestBody Journal journal, @PathVariable UUID journal_uuid) throws ResourceNotExistsException {
        this.journalService.update(journal, journal_uuid);
    }
}
