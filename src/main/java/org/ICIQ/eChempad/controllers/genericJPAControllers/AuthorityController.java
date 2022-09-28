package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.Authority;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;

import java.io.Serializable;
import java.util.UUID;

public interface AuthorityController<T extends JPAEntityImpl, S extends Serializable> extends GenericController<Authority, UUID> {
}
