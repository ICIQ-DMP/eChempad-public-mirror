/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
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

@Service("documentService")
public class DocumentServiceImpl<T extends EntityImpl, S extends Serializable> extends GenericServiceImpl<Document, UUID> implements DocumentService<Document, UUID> {

    @Autowired
    public DocumentServiceImpl(DocumentRepository<T, S> documentRepository, AclServiceCustomImpl aclRepository) {
        super(documentRepository, aclRepository);
    }
    
    @Override
    public Document addDocumentToContainer(Document document, UUID container_uuid) {
        // Obtain lazily loaded container. DB will be accessed if accessing any other field than id
        Container container = super.entityManager.getReference(Container.class, container_uuid);

        // Set the journal of this experiment and sav experiment. Save is cascaded
        document.setParent(container);
        Document documentDB = this.genericRepository.save(document);

        // Add all permissions to document for the current user, and set also inheriting entries for parent experiment
        this.aclRepository.addAllPermissionToLoggedUserInEntity(documentDB, true, container, Container.class);

        return documentDB;
    }


    @Override
    public Set<Document> getDocumentsFromContainer(UUID container_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        // We punctually use the Entity manager to get a Journal entity from the Experiment service
        return super.entityManager.find(Container.class, container_uuid).getChildrenDocuments();
    }

}
