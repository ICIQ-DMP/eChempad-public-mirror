package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.GenericEntity;

import java.io.Serializable;
import java.util.UUID;

public interface AuthorityService<T extends GenericEntity, S extends Serializable> extends GenericService<Authority, UUID> {

}
