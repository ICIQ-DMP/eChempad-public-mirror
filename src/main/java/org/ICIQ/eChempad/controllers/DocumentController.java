package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.exceptions.ExceptionResourceNotExists;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.UUID;

public interface DocumentController {
    ResponseEntity<Set<Document>> getDocuments();

    ResponseEntity<Document> getDocument(UUID uuid) throws ExceptionResourceNotExists;

    void addDocument(Document document);

    void removeDocument(UUID uuid) throws ExceptionResourceNotExists;

    void putDocument(Document document, UUID uuid) throws ExceptionResourceNotExists;
}
