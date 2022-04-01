/**
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
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.UUID;

public interface JournalController {
    /**
     * Obtain all journals accessible by the logged user.
     * @return Set of journals readable by the user.
     */
    ResponseEntity<Set<Journal>> getJournals();

    /**
     * Gets a certain journal identified by the supplied UUID if the logged user has enough permissions to read the
     * journal.
     * @param journal_uuid UUID of the journal we have to retrieve
     * @return Returns the data of the journal that we want to retrieve.
     * @throws ResourceNotExistsException Thrown if the journal with this UUID does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to read the journal.
     */
    ResponseEntity<Journal> getJournal(UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * addJournal(ToResearcher)
     * Adds a new journal to the researcher workspace. We always have permissions for this because it is in our own
     * workspace.
     * @param journal Data of the journal that we are going to add.
     */
    void addJournal(Journal journal);

    /**
     * Obtain all readable journals from a certain researcher, identified by its UUID. We always have permission to
     * query users, but we will only see the journals that we have permission to view.
     * @param researcher_uuid UUID of the researcher that we are querying.
     * @return Returns all the Journals readable of the selected researcher with this UUID.
     * @throws ResourceNotExistsException Thrown if the researcher with the supplied UUID does not exist.
     */
    ResponseEntity<Set<Journal>> getJournalsFromResearcher(UUID researcher_uuid) throws ResourceNotExistsException;

    /**
     * Removes the journal with the supplied UUID. Fails if the journal does not exist or we do not have edition
     * permissions against the journal.
     * @param journal_uuid UUID of the journal that we want to delete.
     * @throws ResourceNotExistsException Thrown if the journal with this UUID does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to remove that journal.
     */
    void removeJournal(UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Updates the journal with the supplied UUID if we have enough permissions and the journal with that UUID exists.
     * @param journal Contains the information of the new journal
     * @param journal_uuid UUID of the journal that we want to update.
     * @throws ResourceNotExistsException Thrown if the journal with the supplied UUID does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have edition authority against this journal.
     */
    void putJournal(Journal journal, UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;
}
