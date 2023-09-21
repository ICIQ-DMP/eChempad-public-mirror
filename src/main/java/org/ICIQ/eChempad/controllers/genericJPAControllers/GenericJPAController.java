/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.Entity;
import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;

import java.io.Serializable;
import java.util.Set;

public interface GenericJPAController<T extends Entity, S extends Serializable> {

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
