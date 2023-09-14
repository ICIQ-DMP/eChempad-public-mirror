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
package org.ICIQ.eChempad.services.importServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Defines a basic contract for all the classes that import data from a third service, such as Signals.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 24/10/2022
 **/
public interface ImportService {

    /**
     * Reads the content that is in the root of the platform that we are reading from.
     *
     * @param APIKey API key used to authenticate in the platform that we are importing from.
     * @return A list of the elements that we read.
     */
    List<DataEntity> readRootEntities(String APIKey);

    /**
     * Updates the corresponding root entity in the database with the data supplied by parameter.
     * - If it is not present it will be saved as a new element.
     * - If it is present and data in eChempad has changes, ignores the update.
     * - If it is present and data in Signals has changes, replaces the entity.
     * - If it is present and both Signals and eChempad has changes, calls the updateEntity function recursively in each
     *   node of the tree, performing this same algorithm. When arriving to leaf level, do nothing if both have changes.
     *
     * @param container Data entity to be imported into the workspace of the user. This entity is expected to be
     *                  unmanaged by hibernate (not saved in the db).
     */
    void updateRootContainer(Container container, String APIKey);

    /**
     * Expands the children of the received DataEntity by performing CRUD requests to read the children of this entity
     * in the origin platform. The children will be added to the DataEntity supplied by parameter. Only immediate
     * children will be expanded so further recursive uses will be needed to read the full hierarchy from the origin
     * platform.
     *
     * @param container The Container that we are going to query in the origin platform for its children, which will
     *                   be added to this parameter to return the entities of the children.
     * @param APIKey API key to authenticate in the platform that we are importing from.
     */
    void expandContainerChildren(Container container, String APIKey);

    /**
     * Expands the children of an entity. If it is a container the entity will be expanded and the function will return
     * true, and if it is not a container (must be a document), then the function does nothing and returns false.
     *
     * @param entity Entity to expand using the read information from Signals.
     * @param APIKey The API key used to authenticate the requests.
     * @return Returns false if the supplied entity is a document and returns true if it is a container.
     */
    boolean expandEntityChildren(DataEntity entity, String APIKey);

    /**
     * Expands the children of a container that has documents as children using the data read from the Signals importing
     * service.
     *
     * @param container Container that we will query in the server for documents.
     * @param APIKey API key used to authenticate the requests.
     */
    void expandContainerDocuments(Container container, String APIKey);

    /**
     * Expands the hierarchy of a container reading data from Signals assuming it is a root container
     * (journal / notebook).
     *
     * @param container A data entity that represents a root container in the importing Signals instance.
     * @param APIKey API key used to authenticate requests.
     */
    void expandRootContainer(Container container, String APIKey);

    /**
     * Expands the children of the received DataEntity recursively by performing CRUD requests to read the children of
     * this entity in the origin platform in a recursive manner. The children and the rest of descendents will be added
     * to the DataEntity supplied by parameter. With a single call, this function adds all the data to the supplied
     * entity from the original platform.
     *  @param container The DataEntity that we are going to query in the origin platform for its children, which will
     *                   be added to this parameter to return the entities of the children.
     * @param APIKey API key to authenticate in the platform that we are importing from.
     */
    void expandContainerHierarchy(Container container, String APIKey);

    /**
     * Receives an unmanaged DataEntity that can be a Document or Container and performs recursion with minimal queries
     * and API requests to get only the necessary data to update the corresponding entity in our database. To know if
     * there is a corresponding entity, we will query the parent for its children. This will work for all branches of
     * the tree, but not for the root containers, which will be signaled as so receiving null in the parent. In this
     * case we will search a possible matching entity by querying the service directly.
     * <p>
     * - If it is not present it will be saved as a new element.
     * - If it is present and data in eChempad has changes, ignores the update.
     * - If it is present and data in Signals has changes, replaces the entity.
     * - If it is present and both Signals and eChempad has changes, calls the mergeEntities function recursively in
     *   each node of the tree, performing this same algorithm. When arriving to leaf level, do nothing if both have
     *   changes.
     * @param parentInDatabase Container entity that is already present in the database of eChempad. Is the parent of
     *                         the corresponding entity in our db to the supplied by parameter.
     * @param dataEntitySignals Data entity that is in Signals. It will be expanded only if necessary on each recursive
     *                          call. This entity is expected to be unmanaged.
     * @param APIKey Key to authenticate requests to Signals.
     */
    void syncEntity(Container parentInDatabase, DataEntity dataEntitySignals, String APIKey);
}
