package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.exceptions.ExceptionResourceNotExists;
import org.ICIQ.eChempad.services.DocumentServiceClass;
import org.ICIQ.eChempad.services.ExperimentServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/document")
public class DocumentControllerClass implements DocumentController{
    private DocumentServiceClass documentServiceClass;

    @Autowired
    public DocumentControllerClass(DocumentServiceClass documentServiceClass) {
        this.documentServiceClass = documentServiceClass;
    }

    @Override
    @GetMapping(
            value = "",
            produces = "application/json"
    )
    public ResponseEntity<Set<Document>> getDocuments() {
        HashSet<Document> documents = new HashSet<>(this.documentServiceClass.getAll());
        return ResponseEntity.ok(documents);
    }

    // https://stackoverflow.com/questions/30967822/when-do-i-use-path-params-vs-query-params-in-a-restful-api
    // https://restfulapi.net/http-methods/
    @Override
    @GetMapping(
            value = "/{id}",
            produces = "application/json"
    )
    public ResponseEntity<Document> getDocument(@PathVariable(value = "id") UUID uuid) throws ExceptionResourceNotExists {
        Document document = this.documentServiceClass.get(uuid);
        return ResponseEntity.ok().body(document);
    }


    @PostMapping(
            value = "",
            consumes = "application/json"
    )
    public void addDocument(@Validated @RequestBody Document document) {
        this.documentServiceClass.saveOrUpdate(document);
    }


    @DeleteMapping(
            value = "/{id}",
            produces = "application/json"
    )
    public void removeDocument(@PathVariable(value = "id") UUID uuid) throws ExceptionResourceNotExists {
        this.documentServiceClass.remove(uuid);
    }

    @PutMapping(
            value = "/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    @Override
    public void putDocument(@Validated @RequestBody Document document, @PathVariable(value = "id") UUID uuid) throws ExceptionResourceNotExists {
        this.documentServiceClass.update(document, uuid);
    }

}
