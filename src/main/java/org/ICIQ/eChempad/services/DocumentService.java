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
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

public interface DocumentService extends GenericService<Document, UUID> {

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


}
