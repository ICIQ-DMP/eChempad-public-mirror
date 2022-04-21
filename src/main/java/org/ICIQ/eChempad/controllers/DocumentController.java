/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.configurations.DocumentHelper;
import org.ICIQ.eChempad.configurations.UploadFileResponse;
import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.UUID;

public interface DocumentController {

    /**
     * Returns all the document readable by the logged user
     * @return Collection of readable documents
     */
    ResponseEntity<Set<Document>> getDocuments();

    /**
     * https://stackoverflow.com/questions/30967822/when-do-i-use-path-params-vs-query-params-in-a-restful-api
     * https://restfulapi.net/http-methods/
     * Returns the desired document if it exists in the DB and if we have permissions to read it.
     * @param document_uuid id of the desired document
     * @return ResponseEntity containing the document desired
     * @throws ResourceNotExistsException Thrown if the UUID does not exist for any document
     * @throws NotEnoughAuthorityException Thrown if we do not have read permissions against the document
     */
    ResponseEntity<Document> getDocument(UUID document_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Obtains the file stream associated with a document
     * @param uuid Used to uniquely identify the document in the DB
     * @return ByteArray response (binary response)
     * @throws ResourceNotExistsException Thrown if the UUID does not exist for any document
     * @throws NotEnoughAuthorityException Thrown if we do not have read permissions against the document
     */
    ResponseEntity<Resource> getDocumentData(UUID uuid, HttpServletRequest request) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Adds a document and its corresponding file to an Experiment designated by its unique UUID. It saves the document
     * metadata into the DB and the file in the filesystem.
     * @param document Metadata of the document, from the body of the request
     * @param experiment_uuid UUID of the experiment where we will add this document
     * @return A file response where we tell the user where he can find this file using a URL
     * @throws ResourceNotExistsException Thrown if the experiment where we will add the document does not exist
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to add documents to this
     *         experiment
     */
    UploadFileResponse addDocumentToExperiment(DocumentHelper document, UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Obtain the documents that are hanging from a certain experiment designated by its UUID
     * @param experiment_uuid UUID of the experiment that we will obtain documents from
     * @return Set of document that belong to this experiment.
     */
    ResponseEntity<Set<Document>> getDocumentsFromExperiment(UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Removes the selected document from the DB and deletes its associated file.
     * @param uuid Identifier of the document that will be removed.
     * @throws ResourceNotExistsException Thrown if the UUID of the supplied experiment does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have EDIT authority to delete documents.
     */
    void removeDocument(UUID uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Updates an existent document if we have enough permissions, includes its file
     * @param document Data of the new document
     * @param file Chunk of data associated with this document
     * @param document_uuid ID of the experiment where we are adding
     * @throws ResourceNotExistsException Thrown if the resource does not exist
     * @throws NotEnoughAuthorityException Thrown if we do not have EDIT authority against the updated document
     */
    void putDocument(Document document, MultipartFile file, UUID document_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

}