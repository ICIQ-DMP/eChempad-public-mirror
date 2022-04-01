/**
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
import org.ICIQ.eChempad.entities.ElementPermission;
import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.repositories.ExperimentRepository;
import org.ICIQ.eChempad.services.DocumentServiceClass;
import org.ICIQ.eChempad.services.FileStorageService;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
public class DocumentControllerClass implements DocumentController{

    @Autowired
    private DocumentServiceClass documentServiceClass;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ExperimentRepository experimentRepository;


    /**
     * Returns all the document readable by the logged user
     * @return Collection of readable documents
     */
    @Override
    @GetMapping(
            value = "/api/document",
            produces = "application/json"
    )
    public ResponseEntity<Set<Document>> getDocuments() {
        HashSet<Document> documents = new HashSet<>(this.documentServiceClass.getAll());
        return ResponseEntity.ok(documents);
    }


    /**
     * Returns the desired document if it exists in the DB and if we have permissions to read it.
     * https://stackoverflow.com/questions/30967822/when-do-i-use-path-params-vs-query-params-in-a-restful-api
     * https://restfulapi.net/http-methods/
     * @param uuid id of the desired document
     * @return ResponseEntity containing the document desired
     * @throws ResourceNotExistsException Thrown if the UUID does not exist for any document
     */
    @Override
    @GetMapping(
            value = "/api/document/{id}",
            produces = "application/json"
    )
    public ResponseEntity<Document> getDocument(@PathVariable(value = "id") UUID uuid) throws ResourceNotExistsException {
        Document document = this.documentServiceClass.get(uuid);
        return ResponseEntity.ok().body(document);
    }


    /**
     * Obtains the file stream associated with a document
     * @param uuid Used to uniquely identify the document in the DB
     * @return ByteArray response (binary response)
     * @throws ResourceNotExistsException Thrown if the UUID does not exist for any document
     */
    @GetMapping("/api/document/{id}/data")
    public ResponseEntity<Resource> getDocumentData(@PathVariable(value = "id") UUID uuid, HttpServletRequest request) throws ResourceNotExistsException{
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(uuid);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            Logger.getGlobal().info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


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
    @PostMapping(
            value = "/api/experiment/{experiment_uuid}/document",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UploadFileResponse addDocumentToExperiment(@ModelAttribute("Document") DocumentHelper document, @PathVariable UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {

        Document document1 = this.documentServiceClass.addDocumentToExperiment(document, experiment_uuid);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/document/")
                .path(document1.getUUid().toString())
                .path("/data")
                .toUriString();

        return new UploadFileResponse(document.getName(), fileDownloadUri,
                document.getFile().getContentType(), document.getFile().getSize());
    }

    /**
     * Obtain the documents that are hanging from a certain experiment designated by its UUID
     *
     * @param experiment_uuid UUID of the experiment that we will obtain documents from
     * @return Set of document that belong to this experiment.
     */
    @Override
    @GetMapping("/api/experiment/{experiment_uuid}/document")
    public ResponseEntity<Set<Document>> getDocumentsFromExperiment(@PathVariable UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        Set<Document> documents = this.documentServiceClass.getDocumentsFromExperiment(experiment_uuid);

        // More properties could be added such as the mimetype (JSON)
        return ResponseEntity.ok(documents);
    }
































    /**
     * Removes the selected document from the DB and deletes its associated file.
     * @param uuid Identifier of the document that will be removed.
     * @throws ResourceNotExistsException Thrown if the UUID of the supplied experiment does not exist.
     * @throws NotEnoughAuthorityException Thrown if we do not have EDIT authority to delete documents.
     */
    @DeleteMapping(
            value = "/api/document/{id}",
            produces = "application/json"
    )
    public void removeDocument(@PathVariable(value = "id") UUID uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        this.documentServiceClass.remove(uuid);
    }


    /**
     * Updates an existent document if we have enough permissions, includes its file
     * @param document Data of the new document
     * @param file Chunk of data associated with this document
     * @param document_uuid ID of the experiment where we are adding
     * @throws ResourceNotExistsException Thrown if the resource does not exist
     * @throws NotEnoughAuthorityException Thrown if we do not have EDIT authority against the updated document
     */
    @PutMapping(
            value = "/api/document/{document_uuid}",
            produces = "application/json",
            consumes = "application/json"
    )
    @Override
    public void putDocument(@Validated @RequestBody Document document, @RequestParam("file") MultipartFile file, @PathVariable(value = "document_uuid") UUID document_uuid) throws ResourceNotExistsException {
        this.documentServiceClass.update(document, document_uuid);
    }

}
