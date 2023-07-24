package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.Authority;
import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;

import java.io.Serializable;
import java.util.UUID;

public interface AuthorityJPAController<T extends EntityImpl, S extends Serializable> extends GenericJPAController<Authority, UUID> {
}
