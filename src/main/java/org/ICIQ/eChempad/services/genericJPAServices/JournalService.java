package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.GenericJPAEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;

import java.io.Serializable;
import java.util.UUID;

/**
 * Non-generic functions used to manipulate the in-memory data structures of the researchers. The generic calls are
 * provided in GenericServiceClass
 */
public interface JournalService<T extends GenericJPAEntity, S extends Serializable> extends GenericService<Journal, UUID> {

}
