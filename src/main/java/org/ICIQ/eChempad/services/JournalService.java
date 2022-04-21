/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;

import java.util.Set;
import java.util.UUID;


/**
 * Contains the specific redefinition of all the methods that we can apply to a journal but rewritten to take in account
 * security concerns and extra logic that does only apply to journal.
 */
public interface JournalService extends GenericService<Journal, UUID> {

    /**
     * Obtains all the journals readable by the logged in user.
     * @return Set of readable journals
     */
    Set<Journal> getJournals();

    /**
     * Obtains the journal identified by its UUID if the logged in reseqarcher has enough permissions to read the
     * journal.
     * @param journal_uuid UUID of the journal we want to read.
     * @return Journal data
     * @throws ResourceNotExistsException Thrown if the journal with this UUID does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to read the journal.
     */
    Journal getJournal(UUID journal_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Adds a new journal to the user workspace. It will be always available since it is on the own user workspace.
     * @param entity Journal data to add.
     */
    void addJournal(Journal entity);

    /**
     * Updates the journal with the UUID supplied with data from the journal
     * @param entity journal data
     * @param id ID of the journal instance that we are updating.
     * @return Returns the journal instance that we have updated.
     * @throws ResourceNotExistsException Thrown if the resource with the supplied UUID does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority (write) to operate against the
     * entity
     */
    Journal update(Journal entity, UUID id) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Removes the journal with the supplied UUID from the database.
     * @param id ID of the journal that we are going to delete.
     * @throws ResourceNotExistsException Thrown if the journal with this ID does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough permissions to operate in this journal.
     */
    void remove(UUID id) throws ResourceNotExistsException, NotEnoughAuthorityException;

}
