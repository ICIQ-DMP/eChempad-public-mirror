package org.ICIQ.eChempad.repositories.genericJPARepositories;

import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.UUID;

@Repository
@Transactional
public interface ResearcherRepository<T extends EntityImpl, S extends Serializable> extends GenericRepository<Researcher, UUID>{

    Researcher findByUsername(String username);
}
