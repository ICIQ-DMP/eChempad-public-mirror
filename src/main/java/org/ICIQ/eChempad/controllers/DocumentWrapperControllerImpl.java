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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/document")
public class DocumentWrapperControllerImpl<T extends JPAEntityImpl, S extends Serializable> extends GenericControllerImpl<DocumentWrapper, UUID> implements DocumentWrapperController<DocumentWrapper, UUID> {

    @Autowired
    public DocumentWrapperControllerImpl(DocumentWrapperService<DocumentWrapper, UUID> documentWrapperService) {
        super(documentWrapperService);
    }

    @DeleteMapping(
            value = "/{id}",
            produces = "application/json"
    )
    @PreAuthorize("hasPermission(#id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Document' , 'DELETE')")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public DocumentWrapper remove(@PathVariable UUID id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        Optional<DocumentWrapper> entity = this.genericService.findById(id);
        if (entity.isPresent()) {
            this.genericService.deleteById(id);
            return entity.get();
        }
        else
            throw new ResourceNotExistsException();
    }

    @Override
    @GetMapping("/{id}/data")
    @PreAuthorize("hasPermission(#id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Document' , 'READ')")
    public ResponseEntity<Resource> getDocumentData(@PathVariable UUID id, HttpServletRequest request) throws ResourceNotExistsException, NotEnoughAuthorityException{
        // Load file as Resource
        Resource resource = ((DocumentWrapperService<T, S>)this.genericService).getById(id).getFile().getResource();

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
            value = "/{experiment_id}/experiment",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasPermission(#experiment_id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Experiment' , 'WRITE')")
    public UploadFileResponse addDocumentToExperiment(@ModelAttribute("Document") DocumentWrapper document, @PathVariable UUID experiment_id) throws ResourceNotExistsException, NotEnoughAuthorityException {

        DocumentWrapper document1 = ((DocumentWrapperService<T, S>)this.genericService).addDocumentToExperiment(document, experiment_id);

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
        return ((DocumentWrapperService<T, S>)this.genericService).getDocumentsFromExperiment(experiment_id);
    }
}
