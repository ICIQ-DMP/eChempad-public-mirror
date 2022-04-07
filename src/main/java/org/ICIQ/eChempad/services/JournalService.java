/**
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
 * security concerns and extra logic that does only apply to journals.
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





    Journal update(Journal entity, UUID id) throws ResourceNotExistsException;




    // Not overridden. We can always add our own journals no matter security concerns
    //void add(T entity);

    void remove(UUID id) throws ResourceNotExistsException;

}
