package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.DocumentWrapper;
import org.ICIQ.eChempad.configurations.wrappers.UploadFileResponse;
import org.ICIQ.eChempad.controllers.genericJPAControllers.GenericController;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public interface DocumentWrapperController<T extends JPAEntityImpl, S extends Serializable> extends GenericController<DocumentWrapper, UUID> {

    ResponseEntity<Resource> getDocumentData(@PathVariable(value = "id") UUID uuid, HttpServletRequest request) throws ResourceNotExistsException, NotEnoughAuthorityException;

    UploadFileResponse addDocumentToExperiment(@ModelAttribute("DocumentWrapper") DocumentWrapper document, @PathVariable UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    Set<DocumentWrapper> getDocumentsFromExperiment(@PathVariable UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;
}
