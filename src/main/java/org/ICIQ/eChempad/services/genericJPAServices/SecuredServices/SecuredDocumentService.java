package org.ICIQ.eChempad.services.genericJPAServices.SecuredServices;

import org.ICIQ.eChempad.configurations.security.ACL.AclServiceCustomImpl;
import org.ICIQ.eChempad.configurations.security.ACL.PermissionBuilder;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.services.genericJPAServices.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service("securedDocumentService")
public class SecuredDocumentService<T extends Document, S extends Serializable> extends SecuredGenericServiceImpl<Document, UUID> implements SecuredGenericService<Document, UUID>, DocumentService<Document, UUID>{

    @Autowired
    public SecuredDocumentService(@Qualifier("documentService") DocumentService<T, S> genericService, PermissionEvaluator permissionEvaluator, AclServiceCustomImpl aclService) {
        super(genericService, permissionEvaluator, aclService);
    }

    public SecuredDocumentService() {
    }

    @Override
    public Class<Document> getEntityClass() {
        return Document.class;
    }

    @Override
    public Document addDocumentToContainer(Document document, UUID container_uuid) {
        // We know that we have a document service, we can cast safely
        Document documentDB = ((DocumentService<T, S>) this.genericService).addDocumentToContainer(document, container_uuid);

        // Add all permissions to document for the current user, and set also inheriting entries for parent experiment
        super.aclService.addPermissionToEntity(documentDB, true, PermissionBuilder.getFullPermission(), null);

        return documentDB;
    }

    @Override
    public Set<Document> getDocumentsFromContainer(UUID container_uuid) {
        return null;
    }

    @Override
    public List<Document> searchByOriginId(String originId) {
        return null;
    }
}
