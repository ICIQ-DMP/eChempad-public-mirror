package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.GenericEntity;
import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.UUID;

/**
 * When extending a JPARepository interface (by extending GenericRepository) DB access methods are automatically
 * IMPLEMENTED regarding the bounded class, the name of the class and / or the name of the method.
 * JPA tries to deduce the suitable implementation by processing the natural language of the class or methods to
 * implement.
 */
@Repository
@Transactional
public interface JournalRepository<T extends GenericEntity, S extends Serializable> extends GenericRepository<Journal, UUID> {

}
