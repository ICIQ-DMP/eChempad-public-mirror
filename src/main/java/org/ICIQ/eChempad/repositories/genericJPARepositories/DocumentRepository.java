package org.ICIQ.eChempad.repositories.genericJPARepositories;

import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.UUID;

@Repository
@Transactional
public interface DocumentRepository<T extends EntityImpl, S extends Serializable> extends GenericRepository<Document, UUID> {
}
