package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.GenericJPAEntity;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;

import java.io.Serializable;
import java.util.Set;

public interface GenericController <T extends GenericJPAEntity, S extends Serializable> {

    /**
     * return the entity class of this generic repository.
     * Note: Default methods are a special Java 8 feature in where interfaces can define implementations for methods.
     * @return Internal class type of this generic repository, set at the creation of the repository.
     */
    Class<T> getEntityClass();

    Set<T> getAll();

    T get(S id) throws ResourceNotExistsException;

    T add(T t);

    T remove(S id) throws ResourceNotExistsException, NotEnoughAuthorityException;

    T put(T t, S id) throws ResourceNotExistsException, NotEnoughAuthorityException;


}
