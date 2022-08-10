package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Journal;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.UUID;

@Repository
public interface AuthorityRepository<T extends IEntity, S extends Serializable> extends GenericRepository<Authority, UUID>{
}
