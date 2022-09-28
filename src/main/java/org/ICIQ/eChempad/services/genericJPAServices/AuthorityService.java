package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Authority;
import org.ICIQ.eChempad.entities.genericJPAEntities.GenericJPAEntity;

import java.io.Serializable;
import java.util.UUID;

public interface AuthorityService<T extends GenericJPAEntity, S extends Serializable> extends GenericService<Authority, UUID> {

}
