package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.configurations.UploadFileResponse;
import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.repositories.DocumentRepository;
import org.ICIQ.eChempad.repositories.ExperimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public void addDocumentToExperiment(Document document, UUID experiment_uuid) {
        Experiment experiment = this.experimentRepository.get(experiment_uuid);

        if (this.securityService.isResearcherAuthorized(Authority.WRITE, experiment_uuid, Experiment.class))
        {
            // Doc point to experiment
            document.setExperiment(experiment);
            // Save the doc
            this.genericRepository.saveOrUpdate(document);
            // Then add to experiment the new doc
            experiment.getDocuments().add(document);
            // And save (update) experiment
            this.experimentRepository.saveOrUpdate(experiment);

            // Create response
            this.fileStorageService.storeFile(document.getFile(), document.getUUid());
        }
        else
        {
            throw new ResourceNotExistsException();
        }
    }


}
