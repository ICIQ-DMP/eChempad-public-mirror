package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.DocumentWrapper;
import org.ICIQ.eChempad.configurations.wrappers.UploadFileResponse;
import org.ICIQ.eChempad.controllers.genericJPAControllers.GenericControllerImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.DocumentWrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/document")
public class DocumentWrapperControllerImpl<T extends JPAEntityImpl, S extends Serializable> implements DocumentWrapperController<DocumentWrapper, UUID> {

    @Autowired
    DocumentWrapperService<DocumentWrapper, UUID> documentWrapperService;


    /**
     * return the entity class of this generic repository.
     * Note: Default methods are a special Java 8 feature in where interfaces can define implementations for methods.
     *
     * @return Internal class type of this generic repository, set at the creation of the repository.
     */
    @Override
    public Class<DocumentWrapper> getEntityClass() {
        return this.documentWrapperService.getEntityClass();
    }

    @GetMapping(
            value = "",
            produces = "application/json"
    )
    @PostFilter("hasPermission(filterObject.id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Document', 'READ')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public Set<DocumentWrapper> getAll() {
        return new HashSet<>(this.documentWrapperService.findAll());
    }



    @GetMapping(
            value = "/{id}",
            produces = "application/json"
    )
    @PostAuthorize("hasPermission(returnObject.id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Document', 'READ')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public DocumentWrapper get(@PathVariable UUID id) throws ResourceNotExistsException {
        Optional<DocumentWrapper> opt = this.documentWrapperService.findById(id);
        return opt.orElse(null);
    }


    @PostMapping(
            value = "",
            produces = "application/json",
            consumes = "multipart/form-data"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public DocumentWrapper add(@Validated @RequestBody @ModelAttribute DocumentWrapper documentWrapper) {
        return this.documentWrapperService.save(documentWrapper);
    }

    @DeleteMapping(
            value = "/{id}",
            produces = "application/json"
    )
    @PreAuthorize("hasPermission(#id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Document' , 'DELETE')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public DocumentWrapper remove(@PathVariable UUID id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        Optional<DocumentWrapper> entity = this.documentWrapperService.findById(id);
        if (entity.isPresent()) {
            this.documentWrapperService.deleteById(id);
            return entity.get();
        }
        else
            throw new ResourceNotExistsException();
    }

    @PutMapping(
            value = "/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    @PreAuthorize("hasPermission(#id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Document', 'WRITE')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public DocumentWrapper put(@Validated @RequestBody DocumentWrapper documentWrapper, @PathVariable(value = "id") UUID id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        documentWrapper.setId(id);
        return this.documentWrapperService.save(documentWrapper);
    }

    @Override
    @GetMapping("/{id}/data")
    @PreAuthorize("hasPermission(#id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Document' , 'READ')")
    public ResponseEntity<Resource> getDocumentData(@PathVariable UUID id, HttpServletRequest request) throws ResourceNotExistsException, NotEnoughAuthorityException{
        // Load file as Resource
        Optional<DocumentWrapper> documentWrapperOptional = this.documentWrapperService.findById(id);
        if (! documentWrapperOptional.isPresent())
        {
            return null;  // TODO map to exception
        }

        // Try to determine file's content type
        String contentType = null;
        contentType = request.getServletContext().getMimeType(documentWrapperOptional.get().getFile().getContentType());

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentWrapperOptional.get().getFile().getOriginalFilename() + "\"")
                .body(documentWrapperOptional.get().getFile().getResource());
    }

    @Override
    @PostMapping(
            value = "/{experiment_id}/experiment",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasPermission(#experiment_id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Experiment' , 'WRITE')")
    public UploadFileResponse addDocumentToExperiment(@ModelAttribute("Document") DocumentWrapper document, @PathVariable UUID experiment_id) throws ResourceNotExistsException, NotEnoughAuthorityException {

        DocumentWrapper document1 = this.documentWrapperService.addDocumentToExperiment(document, experiment_id);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/document/")
                .path(document1.getId().toString())
                .path("/data")
                .toUriString();

        return new UploadFileResponse(document.getName(), fileDownloadUri,
                document.getFile().getContentType(), document.getFile().getSize());
    }

    @Override
    @GetMapping("/{experiment_id}/experiment")
    @PreAuthorize("hasPermission(#experiment_id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Experiment' , 'READ')")
    public Set<DocumentWrapper> getDocumentsFromExperiment(@PathVariable UUID experiment_id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        return this.documentWrapperService.getDocumentsFromExperiment(experiment_id);
    }
}
