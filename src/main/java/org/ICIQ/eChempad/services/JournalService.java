package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;

import java.util.Set;
import java.util.UUID;


/**
 * Contains the specific redefinition of all the methods that we can apply to a journal but rewritten to take in account
 * security concerns and extra logic that does only apply to journals.
 */
public interface JournalService extends GenericService<Journal, UUID> {


    Journal save(Journal entity);

    Journal update(Journal entity, UUID id) throws ResourceNotExistsException;

    Set<Journal> getAll();

    Journal get(UUID id) throws ResourceNotExistsException;

    // Not overridden. We can always add our own journals no matter security concerns
    //void add(T entity);

    void remove(UUID id) throws ResourceNotExistsException;

}
