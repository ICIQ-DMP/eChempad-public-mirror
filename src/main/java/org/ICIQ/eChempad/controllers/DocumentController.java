package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.configurations.UploadFileResponse;
import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DocumentController {

    /**
     * Returns all the document readable by the logged user
     * @return Collection of readable documents
     */
    ResponseEntity<Set<Document>> getDocuments();

    /**
     * Returns the desired document if it exists in the DB and if we have permissions to read it.
     * @param uuid id of the desired document
     * @return ResponseEntity containing the document desired
     * @throws ResourceNotExistsException Thrown if the UUID does not exist for any document
     * @throws NotEnoughAuthorityException Thrown if we do not have read permissions against the document
     */
    ResponseEntity<Document> getDocument(UUID uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Obtains the file stream associated with a document
     * @param uuid Used to uniquely identify the document in the DB
     * @return ByteArray response (binary response)
     * @throws ResourceNotExistsException Thrown if the UUID does not exist for any document
     * @throws NotEnoughAuthorityException Thrown if we do not have read permissions against the document
     */
    ResponseEntity<Resource> getDocumentData(UUID uuid, HttpServletRequest request) throws ResourceNotExistsException, NotEnoughAuthorityException;

    /**
     * Upload the received file if it does not collide with another into the local filesystem
     * @param document Document data coming from the HTTP body
     * @param experiment_uuid Experiment where we will add the received document.
     * @return Response containing the relevant information of the uploaded file, such as the download URL.
     * @throws ResourceNotExistsException Thrown if the UUID of the supplied experiment does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have enough authority to write in the desired experiment
     */
    UploadFileResponse addDocument(Document document, UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

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