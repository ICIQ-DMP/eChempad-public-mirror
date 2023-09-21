package org.ICIQ.eChempad.services.genericJPAServices.SecuredServices;

import org.ICIQ.eChempad.configurations.security.ACL.AclServiceCustomImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Entity;
import org.ICIQ.eChempad.services.genericJPAServices.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service("securedContainerService")
public class SecuredContainerService<T extends Container, S extends Serializable> extends SecuredGenericServiceImpl<Container, UUID> implements SecuredGenericService<Container, UUID>, ContainerService<Container, UUID> {

    @Autowired
    public SecuredContainerService(@Qualifier("containerService") ContainerService<T, S> genericService, PermissionEvaluator permissionEvaluator, AclServiceCustomImpl aclService) {
        super(genericService, permissionEvaluator, aclService);
    }

    public SecuredContainerService() {
    }

    @Override
    public Class<Container> getEntityClass() {
        return Container.class;
    }

    @Override
    public Set<Container> getChildrenContainers(UUID uuid_container) {
        return null;
    }

    @Override
    public Set<Document> getChildrenDocuments(UUID uuid_container) {
        return null;
    }

    @Override
    public Set<Entity> getChildren(UUID uuid_container) {
        return null;
    }

    @Override
    public void addContainersToContainer(Set<Container> newChildren, UUID uuid_container) {

    }

    @Override
    public void addDocumentsToContainer(Set<Document> newChildren, UUID uuid_container) {

    }

    @Override
    public void addEntitiesToContainer(Set<Entity> newChildren, UUID uuid_container) {

    }

    @Override
    public List<Container> searchByOriginId(String originId) {
        return null;
    }
}
