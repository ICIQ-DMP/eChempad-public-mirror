/**
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.configurations.DocumentHelper;
import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.Set;
import java.util.UUID;

public interface DocumentService extends GenericService<Document, UUID> {

    /**
     * Returns all the document readable by the logged user.
     * @return Collection of readable documents.
     */
    Set<Document> getDocuments();

    /**
     * Returns the desired document if it exists in the DB and if we have permissions to read it from the logged user.
     * @param uuid UUID of the desired document.
     * @return Data of the document.
     * @throws ResourceNotExistsException Thrown if the UUID does not exist for any document.
     * @throws NotEnoughAuthorityException Thrown if we do not have read permissions against the document.
     */
    Document getDocument(@PathVariable(value = "id") UUID uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Adds a document to a certain experiment using the data in the document helper class and returns the new Document
     * instance.
     * @param document Data of a detached document instance inside a helper class that should have all the equivalent
     *                 fields.
     * @param experiment_uuid UUID of the experiment that we want to edit by adding this document.
     * @return Managed instance of the created document.
     */
    Document addDocumentToExperiment(DocumentHelper document, UUID experiment_uuid);

    /**
     * Get all documents that belong to a certain experiment designated by its UUID.
     * @param experiment_uuid UUID of the experiment we are retrieving.
     * @return Set of documents of this experiment. It could be empty.
     */
    Set<Document> getDocumentsFromExperiment(UUID experiment_uuid);

    /**
     * Obtains the file stream associated with a document if there is enough permissions to read it.
     * @param document_uuid Used to uniquely identify the document in the file DB.
     * @return ByteArray response (binary response).
     * @throws ResourceNotExistsException Thrown if the UUID does not exist for any document.
     * @throws NotEnoughAuthorityException Thrown if we do not have read permissions against the document.
     */
    Resource getDocumentData(UUID document_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;


}
