package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.exceptions.ExceptionResourceNotExists;
import org.ICIQ.eChempad.services.DocumentServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/document")
public class DocumentControllerClass implements DocumentController{
    private final DocumentServiceClass documentServiceClass;

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

    // https://stackoverflow.com/questions/30967822/when-do-i-use-path-params-vs-query-params-in-a-restful-api
    // https://restfulapi.net/http-methods/
    @Override
    @GetMapping(
            value = "/{id}/data",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<ByteArrayResource> getDocumentData(@PathVariable(value = "id") UUID uuid) throws ExceptionResourceNotExists {
        Path path = this.documentServiceClass.get(uuid).getPath();
        File file = path.toFile();
        try {
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            return ResponseEntity
                    .ok()
                    .contentLength(file.length())
                    .header("Content-Disposition", "attachment;filename=" + file.getName())
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @Override
    @PostMapping(
            value = "/{id}/data",
            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public void addDocumentData(@PathVariable(value = "id") UUID uuid, @RequestBody ByteArrayResource byteArrayResource) throws ExceptionResourceNotExists {
        Document document = this.documentServiceClass.get(uuid);  // Obtain document from DB

        System.out.print("addding document");
        Path path = Paths.get("/home/amarine/Videos/" + uuid.toString());

        try {
            Files.write(path, byteArrayResource.getByteArray());
            document.setPath(path);
            this.documentServiceClass.save(document);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @PostMapping(
            value = "",
            consumes = "application/json"
    )
    public void addDocument(@Validated @RequestBody Document document) {
        this.documentServiceClass.save(document);
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
