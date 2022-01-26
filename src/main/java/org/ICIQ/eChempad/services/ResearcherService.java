package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;

import java.util.UUID;

/**
 * Non-generic functions used to manipulate the in-memory data structures of the researchers. The generic calls are
 * provided in GenericServiceClass
 */
public interface ResearcherService extends GenericService<Researcher, UUID> {

}
