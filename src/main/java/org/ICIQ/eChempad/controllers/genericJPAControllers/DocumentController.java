package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.configurations.wrappers.DocumentWrapper;
import org.ICIQ.eChempad.configurations.wrappers.UploadFileResponse;
import org.ICIQ.eChempad.entities.genericJPAEntities.Authority;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.dom4j.DocumentHelper;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public interface DocumentController<T extends JPAEntityImpl, S extends Serializable> extends GenericController<Document, UUID> {

    public ResponseEntity<Resource> getDocumentData(@PathVariable(value = "id") UUID uuid, HttpServletRequest request) throws ResourceNotExistsException, NotEnoughAuthorityException;

    public UploadFileResponse addDocumentToExperiment(@ModelAttribute("Document") DocumentWrapper document, @PathVariable UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    public Set<Document> getDocumentsFromExperiment(@PathVariable UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;
}
