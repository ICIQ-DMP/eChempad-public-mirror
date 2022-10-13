package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.configurations.security.ACL.AclServiceCustomImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.*;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.repositories.genericJPARepositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Service
public class DocumentServiceImpl<T extends JPAEntityImpl, S extends Serializable> extends GenericServiceImpl<Document, UUID> implements DocumentService<Document, UUID> {

    @Autowired
    public DocumentServiceImpl(DocumentRepository<T, S> documentRepository, AclServiceCustomImpl aclRepository) {
        super(documentRepository, aclRepository);
    }
    
    @Override
    public Document addDocumentToExperiment(Document document, UUID experiment_uuid) {
        // Obtain lazily loaded journal. DB will be accessed if accessing any other field than id
        Experiment experiment = super.entityManager.getReference(Experiment.class, experiment_uuid);

        // Set the journal of this experiment and sav experiment. Save is cascaded
        document.setExperiment(experiment);
        Document documentDB = this.genericRepository.save(document);

        // Add all permissions to document for the current user, and set also inheriting entries for parent experiment
        this.aclRepository.addAllPermissionToLoggedUserInEntity(documentDB, true, experiment, Experiment.class);

        return documentDB;
    }


    @Override
    public Set<Document> getDocumentsFromExperiment(UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        // We punctually use the Entity manager to get a Journal entity from the Experiment service
        return super.entityManager.find(Experiment.class, experiment_uuid).getDocuments();
    }

}
