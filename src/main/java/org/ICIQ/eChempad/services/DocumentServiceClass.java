/**
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.configurations.DocumentHelper;
import org.ICIQ.eChempad.configurations.UploadFileResponse;
import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.repositories.DocumentRepository;
import org.ICIQ.eChempad.repositories.ExperimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.UUID;

@Service
public class DocumentServiceClass extends GenericServiceClass<Document, UUID> implements DocumentService{

    @Autowired
    SecurityService securityService;

    @Autowired
    ExperimentRepository experimentRepository;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    public DocumentServiceClass(DocumentRepository documentRepository) {
        super(documentRepository);
    }

    @Override
    public Set<Document> getAll() {
        return this.securityService.getAuthorizedDocument(Authority.READ);
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


}
