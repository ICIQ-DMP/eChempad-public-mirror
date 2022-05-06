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
import org.ICIQ.eChempad.services.DocumentServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@RestController
public class DocumentControllerClass implements DocumentController{

    private final DocumentServiceClass documentServiceClass;

    @Autowired
    public DocumentControllerClass(DocumentServiceClass documentServiceClass) {
        this.documentServiceClass = documentServiceClass;
    }

    @Override
    @GetMapping(
            value = "/api/document",
            produces = "application/json"
    )
    public ResponseEntity<Set<Document>> getDocuments() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            auth.getAuthorities().stream().forEach((t) -> System.out.println(t.getAuthority().toString()));
        }




        HashSet<Document> documents = new HashSet<>(this.documentServiceClass.getDocuments());
        return ResponseEntity.ok(documents);
    }

    @Override
    @GetMapping(
            value = "/api/document/{id}",
            produces = "application/json"
    )
    public ResponseEntity<Document> getDocument(@PathVariable(value = "id") UUID document_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        Document document = this.documentServiceClass.getDocument(document_uuid);
        return ResponseEntity.ok().body(document);
    }

    @Override
    @GetMapping("/api/document/{id}/data")
    public ResponseEntity<Resource> getDocumentData(@PathVariable(value = "id") UUID uuid, HttpServletRequest request) throws ResourceNotExistsException, NotEnoughAuthorityException{
        // Load file as Resource
        Resource resource = this.documentServiceClass.getDocumentData(uuid);

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

    @Override
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

    @Override
    @GetMapping("/api/experiment/{experiment_uuid}/document")
    public ResponseEntity<Set<Document>> getDocumentsFromExperiment(@PathVariable UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        Set<Document> documents = this.documentServiceClass.getDocumentsFromExperiment(experiment_uuid);

        // TODO: More properties could be added such as the mimetype (JSON)
        return ResponseEntity.ok(documents);
    }

    @Override
    @DeleteMapping(
            value = "/api/document/{id}",
            produces = "application/json"
    )
    public void removeDocument(@PathVariable(value = "id") UUID uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        this.documentServiceClass.remove(uuid);
    }

    @Override
    @PutMapping(
            value = "/api/document/{document_uuid}",
            produces = "application/json",
            consumes = "application/json"
    )
    public void putDocument(@Validated @RequestBody Document document, @RequestParam("file") MultipartFile file, @PathVariable(value = "document_uuid") UUID document_uuid) throws ResourceNotExistsException {
        this.documentServiceClass.update(document, document_uuid);
    }

}
