/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.configurations.DocumentHelper;
import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.repositories.DocumentRepository;
import org.ICIQ.eChempad.repositories.ExperimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class DocumentServiceClass extends GenericServiceClass<Document, UUID> implements DocumentService{

    final SecurityService securityService;
    final ExperimentRepository experimentRepository;
    final FileStorageService fileStorageService;

    @Autowired
    public DocumentServiceClass(DocumentRepository documentRepository, SecurityService securityService, ExperimentRepository experimentRepository, FileStorageService fileStorageService) {
        super(documentRepository);
        this.securityService = securityService;
        this.experimentRepository = experimentRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public Set<Document> getDocuments() {
        return this.securityService.getAuthorizedDocument(Authority.READ);
    }

    @Override
    public Document getDocument(UUID document_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        Document document = this.genericRepository.get(document_uuid);
        if (this.securityService.isResearcherAuthorized(Authority.READ, document_uuid, Document.class))
        {
            return document;
        }
        else
        {
            throw new NotEnoughAuthorityException("You are not authorized to see this document");
        }
    }

    @Override
    public Document addDocumentToExperiment(DocumentHelper documentHelper, UUID experiment_uuid) {
        Experiment experiment = this.experimentRepository.get(experiment_uuid);

        if (this.securityService.isResearcherAuthorized(Authority.WRITE, experiment_uuid, Experiment.class))
        {
            // Regenerate document and make it point to the experiment
            Document document = new Document(documentHelper.getName(), documentHelper.getDescription(), experiment);
            // Save the doc to obtain its UUID and persist it in the DB
            document = this.genericRepository.saveOrUpdate(document);
            // Then make the experiment have this document
            experiment.getDocuments().add(document);
            // And save (update) experiment
            this.experimentRepository.saveOrUpdate(experiment);

            // Store file and obtain path to store it in the document instance and update instance.
            String path = this.fileStorageService.storeFile(documentHelper.getFile(), document.getUUid());
            document.setPath(path);
            this.genericRepository.saveOrUpdate(document);

            return document;
        }
        else
        {
            throw new NotEnoughAuthorityException("Not enough authority to write in this experiment");
        }
    }

    @Override
    public Set<Document> getDocumentsFromExperiment(UUID experiment_uuid) {
        Experiment experiment = this.experimentRepository.get(experiment_uuid);

        if (this.securityService.isResearcherAuthorized(Authority.READ, experiment_uuid, Experiment.class))
        {
            return experiment.getDocuments();
        }
        else
        {
            throw new NotEnoughAuthorityException("Not enough authority to read documents from this experiment");
        }
    }

    @Override
    public Resource getDocumentData(UUID document_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {

        if (this.securityService.isResearcherAuthorized(Authority.READ, document_uuid, Document.class))
        {
            return this.fileStorageService.loadFileAsResource(document_uuid);
        }
        else
        {
            throw new NotEnoughAuthorityException("Not enough authority to read this document");
        }
    }


}
