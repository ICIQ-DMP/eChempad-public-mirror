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
package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Entity;
import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * Non-generic functions used to manipulate the in-memory data structures of the researchers. The generic calls are
 * provided in GenericServiceClass
 */
public interface ContainerService<T extends EntityImpl, S extends Serializable> extends GenericService<Container, UUID> {

    /**
     * Returns the children container of the container selected by the supplied UUID.
     *
     * @param uuid_container UUID that identifies the container.
     * @return Set of container children of the selected entity.
     */
    Set<Container> getChildrenContainers(UUID uuid_container);

    /**
     * Returns the children documents of the container selected by the supplied UUID.
     *
     * @param uuid_container UUID that identifies the container.
     * @return Set of children documents of the selected container.
     */
    Set<Document> getChildrenDocuments(UUID uuid_container);

    /**
     * Returns all the children of a container selected by the supplied UUID. It mixes the types Container and Document
     * into a Set of Entities.
     *
     * @param uuid_container UUID that identifies the container.
     * @return Set of children Entities (Document and Container) of the selected container.
     */
    Set<Entity> getChildren(UUID uuid_container);

    /**
     * Adds all passed containers to the selected container using its UUID.
     *
     * @param uuid_container UUID that identifies the container.
     */
    void addContainersToContainer(Set<Container> newChildren, UUID uuid_container);

    /**
     * Adds all passed documents to the selected container using its UUID.
     *
     * @param uuid_container UUID that identifies the container.
     */
    void addDocumentsToContainer(Set<Document> newChildren, UUID uuid_container);

    /**
     * Adds all passed entities to the selected container using its UUID.
     *
     * @param uuid_container UUID that identifies the container.
     */
    void addEntitiesToContainer(Set<Entity> newChildren, UUID uuid_container);
}
