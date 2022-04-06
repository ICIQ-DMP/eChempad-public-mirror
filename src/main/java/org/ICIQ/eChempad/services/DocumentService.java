/**
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.configurations.DocumentHelper;
import org.ICIQ.eChempad.configurations.UploadFileResponse;
import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.UUID;

public interface DocumentService extends GenericService<Document, UUID> {

    /**
     * Returns all the document readable by the logged user
     * @return Collection of readable documents
     */
    Set<Document> getAll();

    /**
     * Adds a document to a certain experiment using the data in the document helper class and returns the new Document
     * instance.
     * @param document Data of the document inside a helper class that should have all the equivalent fields.
     * @param experiment_uuid UUID of the experiment that we want to edit by adding this document.
     * @return Managed instance of the created document.
     */
    Document addDocumentToExperiment(DocumentHelper document, UUID experiment_uuid);

    /**
     * Get all documents that belong to a certain experiment designated by its UUID
     * @param experiment_uuid UUID of the experiment we are observing
     * @return Set of documents of this experiment. It could be empty
     */
    Set<Document> getDocumentsFromExperiment(UUID experiment_uuid);

    /**
     * Obtains the file stream associated with a document if there is enough permissions to read it.
     * @param document_uuid Used to uniquely identify the document in the DB
     * @return ByteArray response (binary response)
     * @throws ResourceNotExistsException Thrown if the UUID does not exist for any document
     * @throws NotEnoughAuthorityException Thrown if we do not have read permissions against the document
     */
    Resource getDocumentData(UUID document_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;


}
