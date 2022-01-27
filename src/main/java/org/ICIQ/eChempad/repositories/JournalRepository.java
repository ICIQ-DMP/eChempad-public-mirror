package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Journal;

import java.io.Serializable;
import java.util.UUID;

public interface JournalRepository extends GenericRepository<Journal, UUID>{
    public String getDescription(UUID id);
    public String getName(UUID id);
}
