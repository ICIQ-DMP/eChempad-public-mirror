package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.repositories.DocumentRepository;
import org.ICIQ.eChempad.repositories.ExperimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class DocumentServiceClass extends GenericServiceClass<Document, UUID> implements DocumentService{

    @Autowired
    SecurityService securityService;


    @Autowired
    public DocumentServiceClass(DocumentRepository documentRepository) {
        super(documentRepository);
    }

    @Override
    public Set<Document> getAll() {
        return this.securityService.getAuthorizedDocument(Authority.READ);
    }
}
