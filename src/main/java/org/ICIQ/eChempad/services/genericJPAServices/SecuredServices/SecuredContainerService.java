package org.ICIQ.eChempad.services.genericJPAServices.SecuredServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.services.genericJPAServices.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service("securedContainerService")
public class SecuredContainerService<T extends Container, S extends Serializable> extends SecuredGenericServiceImpl<T, S> implements SecuredGenericService<T, S>{

    @Autowired
    public SecuredContainerService(@Qualifier("containerService") GenericService<T, S> genericService, PermissionEvaluator permissionEvaluator) {
        super(genericService, permissionEvaluator);
    }

    public SecuredContainerService() {
    }

    @Override
    public Class<T> getEntityClass() {
        return (Class<T>) Container.class;
    }
}
