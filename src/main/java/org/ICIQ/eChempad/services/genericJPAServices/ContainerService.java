/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
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
