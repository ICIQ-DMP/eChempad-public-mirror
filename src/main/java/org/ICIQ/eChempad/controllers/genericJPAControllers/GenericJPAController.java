/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;

import java.io.Serializable;
import java.util.Set;

public interface GenericJPAController<T extends JPAEntityImpl, S extends Serializable> {

    /**
     * return the entity class of this generic repository.
     * Note: Default methods are a special Java 8 feature in where interfaces can define implementations for methods.
     * @return Internal class type of this generic repository, set at the creation of the repository.
     */
    Class<T> getEntityClass();

    Set<T> getAll();

    T get(S id) throws ResourceNotExistsException;

    T add(T t);

    /**
     * Delete has to be hardcoded on each class because we do not have the information of the erasure. We actually do,
     * this information is in the URL mapping in runtime, but I do not know how to reference that information from the
     * Spring Security expression that controls the authorization.
     * @param id Id of the entity that we want to remove
     * @return The removed entity
     * @throws ResourceNotExistsException Thrown if the resource does not exist.
     * @throws NotEnoughAuthorityException Thrown if there is not enough authority to perform the action.
     */
    T remove(S id) throws ResourceNotExistsException, NotEnoughAuthorityException;

    T put(T t, S id) throws ResourceNotExistsException, NotEnoughAuthorityException;


}
