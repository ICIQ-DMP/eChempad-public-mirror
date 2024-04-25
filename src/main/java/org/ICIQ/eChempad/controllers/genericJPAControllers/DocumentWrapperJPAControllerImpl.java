/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.DocumentWrapper;
import org.ICIQ.eChempad.configurations.wrappers.UploadFileResponse;
import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;
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

import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/document")
public class DocumentWrapperJPAControllerImpl<T extends EntityImpl, S extends Serializable> implements DocumentWrapperJPAController<DocumentWrapper, UUID> {

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

        if (opt.isPresent())
        {
            return opt.get();
        }
        else
        {
            throw new ResourceNotExistsException();
        }
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
    public UploadFileResponse addDocumentToExperiment(@ModelAttribute("Document") DocumentWrapper documentWrapper, @PathVariable UUID experiment_id) throws ResourceNotExistsException, NotEnoughAuthorityException {

        DocumentWrapper documentWrapper1 = this.documentWrapperService.addDocumentToExperiment(documentWrapper, experiment_id);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/document/")
                .path(documentWrapper1.getId().toString())
                .path("/data")
                .toUriString();

        return new UploadFileResponse(documentWrapper.getName(), fileDownloadUri,
                documentWrapper.getFile().getContentType(), documentWrapper.getFile().getSize());
    }

    @Override
    @GetMapping("/{experiment_id}/experiment")
    @PreAuthorize("hasPermission(#experiment_id, 'org.ICIQ.eChempad.entities.genericJPAEntities.Experiment' , 'READ')")
    public Set<DocumentWrapper> getDocumentsFromExperiment(@PathVariable UUID experiment_id) throws ResourceNotExistsException, NotEnoughAuthorityException {
        return this.documentWrapperService.getDocumentsFromExperiment(experiment_id);
    }
}
