package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.exceptions.ExceptionResourceNotExists;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

public interface DocumentController {
    ResponseEntity<Set<Document>> getDocuments();

    ResponseEntity<Document> getDocument(UUID uuid) throws ExceptionResourceNotExists;

    ResponseEntity<ByteArrayResource> getDocumentData(UUID uuid) throws ExceptionResourceNotExists;

    void addDocument(Document document);

    void addDocumentData(UUID uuid, ByteArrayResource byteArrayResource) throws ExceptionResourceNotExists;

    void removeDocument(UUID uuid) throws ExceptionResourceNotExists;

    void putDocument(Document document, UUID uuid) throws ExceptionResourceNotExists;
}
