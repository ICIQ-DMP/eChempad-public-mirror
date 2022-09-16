package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.GenericEntity;
import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Journal;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.UUID;

@Repository
@Transactional
public interface AuthorityRepository<T extends GenericEntity, S extends Serializable> extends GenericRepository<Authority, UUID>{
}
