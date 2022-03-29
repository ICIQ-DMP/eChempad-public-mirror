package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.UUID;

public interface JournalController {
    ResponseEntity<Set<Journal>> getAllJournals();

    ResponseEntity<Journal> getJournal(UUID uuid) throws ResourceNotExistsException;

    void addJournal(Journal journal);

    void removeJournal(UUID uuid) throws ResourceNotExistsException;

    void putJournal(Journal journal, UUID uuid) throws ResourceNotExistsException;
}
