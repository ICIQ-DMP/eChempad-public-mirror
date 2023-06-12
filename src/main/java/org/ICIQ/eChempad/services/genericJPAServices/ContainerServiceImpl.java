/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Entity;
import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;
import org.ICIQ.eChempad.configurations.security.ACL.AclServiceCustomImpl;
import org.ICIQ.eChempad.repositories.genericJPARepositories.ContainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service("containerService")
public class ContainerServiceImpl<T extends EntityImpl, S extends Serializable> extends GenericServiceImpl<Container, UUID> implements ContainerService<Container, UUID> {

    @Autowired
    public ContainerServiceImpl(ContainerRepository<T, S> containerRepository, AclServiceCustomImpl aclRepository) {
        super(containerRepository, aclRepository);
    }

    @Override
    public Set<Container> getChildrenContainers(UUID uuid_container) {
        return this.genericRepository.getById(uuid_container).getChildrenContainers();
    }

    @Override
    public Set<Document> getChildrenDocuments(UUID uuid_container) {
        return this.genericRepository.getById(uuid_container).getChildrenDocuments();
    }

    @Override
    public Set<Entity> getChildren(UUID uuid_container) {
        Set<Entity> childrenEntities = new HashSet<>(this.getChildrenDocuments(uuid_container));
        childrenEntities.addAll(this.getChildrenContainers(uuid_container));
        return childrenEntities;
    }

    @Override
    public void addContainersToContainer(Set<Container> newChildren, UUID uuid_container) {
        Set<Container> childrenContainers = this.genericRepository.getById(uuid_container).getChildrenContainers();
        childrenContainers.addAll(newChildren);
    }

    @Override
    public void addDocumentsToContainer(Set<Document> newChildren, UUID uuid_container) {
        Set<Document> childrenContainers = this.genericRepository.getById(uuid_container).getChildrenDocuments();
        childrenContainers.addAll(newChildren);
    }

    @Override
    public void addEntitiesToContainer(Set<Entity> newChildren, UUID uuid_container) {
        Set<Entity> children = this.getChildren(uuid_container);
        children.addAll(newChildren);
    }
}
