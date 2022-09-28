package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Authority;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;

import java.io.Serializable;
import java.util.UUID;

public interface AuthorityService<T extends JPAEntityImpl, S extends Serializable> extends GenericService<Authority, UUID> {

}
