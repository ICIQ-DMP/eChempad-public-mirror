package org.ICIQ.eChempad.services.genericJPAServices.SecuredServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.services.genericJPAServices.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("securedDocumentService")
public class SecuredDocumentService<T extends Document, S extends Serializable> extends SecuredGenericServiceImpl<T, S> implements SecuredGenericService<T, S> {

    @Autowired
    public SecuredDocumentService(@Qualifier("documentService") GenericService<T, S> genericService, PermissionEvaluator permissionEvaluator) {
        super(genericService, permissionEvaluator);
    }

    public SecuredDocumentService() {
    }

    @Override
    public Class<T> getEntityClass() {
        return (Class<T>) Document.class;
    }
}
