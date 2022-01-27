package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.exceptions.ExceptionResourceNotExists;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.UUID;

public interface JournalController {
    ResponseEntity<Set<Journal>> getAllJournals();

    ResponseEntity<Journal> getJournal(UUID uuid) throws ExceptionResourceNotExists;

    void addJournal(Journal journal);

    void removeJournal(UUID uuid) throws ExceptionResourceNotExists;

    void putJournal(Journal journal, UUID uuid) throws ExceptionResourceNotExists;
}
