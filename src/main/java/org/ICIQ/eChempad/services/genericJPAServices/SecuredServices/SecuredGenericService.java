package org.ICIQ.eChempad.services.genericJPAServices.SecuredServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Entity;
import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;
import org.ICIQ.eChempad.services.genericJPAServices.GenericService;

import java.io.Serializable;

/**
 * This is an interface that has the same contract as a GenericService, but the implementation differs because the
 * implementation is secured. This class takes into account the logged user and applies programmatically the same
 * filters that already do exist in the controller layer using decorators such as @PostFilter which use the ACL
 * infrastructure.
 *
 * @param <T>
 * @param <S>
 */
public interface SecuredGenericService<T extends Entity, S extends Serializable> extends GenericService<T, S> {

}
