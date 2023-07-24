package org.ICIQ.eChempad.repositories.genericJPARepositories;

import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.UUID;

@Repository
@Transactional
public interface ContainerRepository<T extends EntityImpl, S extends Serializable> extends GenericRepository<Container, UUID> {

}
