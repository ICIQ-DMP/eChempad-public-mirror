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


    /**
     * Obtain all journals accessible by the logged user.
     * @return Set of journals readable by the user.
     */
    @Override
    @GetMapping(
            value = "/api/journal",
            produces = "application/json"
    )
    public ResponseEntity<Set<Journal>> getJournals() {
        ResponseEntity<Set<Journal>> res = ResponseEntity.ok(this.journalService.getJournals());
        return res;
    }


     /**
     * https://stackoverflow.com/questions/30967822/when-do-i-use-path-params-vs-query-params-in-a-restful-api
     * https://restfulapi.net/http-methods/
     * Gets a certain journal identified by the supplied UUID if the logged user has enough permissions to read the
     * journal.
     * @param journal_uuid UUID of the journal we have to retrieve
     * @return Returns the data of the journal that we want to retrieve.
     * @throws ResourceNotExistsException Thrown if the journal with this UUID does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to read the journal.
     */
    @Override
    @GetMapping(
            value = "/api/journal/{journal_uuid}",
            produces = "application/json"
    )
    public ResponseEntity<Journal> getJournal(@PathVariable UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        return ResponseEntity.ok().body(this.journalService.getAll(journal_uuid));
    }


    /**
     * addJournal(ToResearcher)
     * Adds a new journal to the researcher workspace. We always have permissions for this because it is in our own
     * workspace.
     * @param journal Data of the journal that we are going to add.
     */
    @PostMapping(
            value = "/api/journal",
            consumes = "application/json"
    )
    public void addJournal(@Validated @RequestBody Journal journal) {
        this.journalService.save(journal);
    }


    /**
     * Obtain all readable journals from a certain researcher, identified by its UUID. We always have permission to
     * query users, but we will only see the journals that we have permission to view.
     * @param researcher_uuid UUID of the researcher that we are querying.
     * @return Returns all the Journals readable of the selected researcher with this UUID.
     * @throws ResourceNotExistsException Thrown if the researcher with the supplied UUID does not exist.
     */
    @GetMapping(
            value = "/api/researcher/{researcher_uuid}/journal",
            consumes = "application/json"
    )
    @Override
    public ResponseEntity<Set<Journal>> getJournalsFromResearcher(@PathVariable UUID researcher_uuid) throws ResourceNotExistsException {
        return null;
        // TODO
    }




















    /**
     * Removes the journal with the supplied UUID. Fails if the journal does not exist, or we do not have edition
     * permissions against the journal.
     * @param journal_uuid UUID of the journal that we want to delete.
     * @throws ResourceNotExistsException Thrown if the journal with this UUID does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to remove that journal.
     */
    @DeleteMapping(
            value = "/api/journal/{journal_uuid}",
            produces = "application/json"
    )
    public void removeJournal(@PathVariable UUID journal_uuid) throws ResourceNotExistsException {
        this.journalService.remove(journal_uuid);
    }


    /**
     * Updates the journal with the supplied UUID if we have enough permissions and the journal with that UUID exists.
     * @param journal Contains the information of the new journal
     * @param journal_uuid UUID of the journal that we want to update.
     * @throws ResourceNotExistsException Thrown if the journal with the supplied UUID does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have edition authority against this journal.
     */
    @PutMapping(
            value = "/api/journal/{journal_uuid}",
            produces = "application/json",
            consumes = "application/json"
    )
    @Override
    public void putJournal(@Validated @RequestBody Journal journal, @PathVariable UUID journal_uuid) throws ResourceNotExistsException {
        this.journalService.update(journal, journal_uuid);
    }
}
