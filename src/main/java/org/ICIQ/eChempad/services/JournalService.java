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
    Set<Journal> getAll();


    /**
     * Obtains the journal identified
     * @param id
     * @return
     * @throws ResourceNotExistsException
     * @throws NotEnoughAuthorityException
     */
    Journal get(UUID id) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Adds a new journal to the user workspace. It will be always available since it is on the own user workspace.
     * @param entity Journal data to add.
     */
    void add(Journal entity);



    Journal update(Journal entity, UUID id) throws ResourceNotExistsException;




    // Not overridden. We can always add our own journals no matter security concerns
    //void add(T entity);

    void remove(UUID id) throws ResourceNotExistsException;

}
