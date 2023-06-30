/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public interface DocumentService<T extends EntityImpl, S extends Serializable> extends GenericService<Document, UUID> {

    /**
     * Adds a document to a certain experiment using the data in the document helper class and returns the new Document
     * instance.
     * @param document Data of a detached document instance inside a helper class that should have all the equivalent
     *                 fields.
     * @param container_uuid UUID of the experiment that we want to edit by adding this document.
     * @return Managed instance of the created document.
     */
    Document addDocumentToContainer(Document document, UUID container_uuid);

    /**
     * Get all documents that belong to a certain container designated by its UUID.
     * @param container_uuid UUID of the container we are retrieving.
     * @return Set of documents of this container. It could be empty.
     */
    Set<Document> getDocumentsFromContainer(UUID container_uuid);

}
