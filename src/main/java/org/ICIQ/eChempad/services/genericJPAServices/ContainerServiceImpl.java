/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.configurations.database.HibernateUtil;
import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Entity;
import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;
import org.ICIQ.eChempad.configurations.security.ACL.AclServiceCustomImpl;
import org.ICIQ.eChempad.repositories.genericJPARepositories.ContainerRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@Service("containerService")
public class ContainerServiceImpl<T extends Entity, S extends Serializable> extends GenericServiceImpl<Container, UUID> implements ContainerService<Container, UUID> {

    @Autowired
    public ContainerServiceImpl(ContainerRepository<T, S> containerRepository) {
        super(containerRepository);
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

    @Transactional
    @Override
    public void addContainersToContainer(Set<Container> newChildren, UUID uuid_container) {
        Container container = this.genericRepository.getById(uuid_container);
        for (Container containerChildren: newChildren)
        {
            containerChildren.setParent(container);
            this.genericRepository.save(containerChildren);
        }

        container.getChildrenContainers().addAll(newChildren);
        this.genericRepository.save(container);
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

    @Override
    public List<Container> searchByOriginId(String originId) {
        // Create matcher for the originId
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher("originId", ExampleMatcher.GenericPropertyMatchers.exact().caseSensitive());

        /*
         Create an empty container with the originId to match the entities with same originId from our database using
         the "example" query method
         */
        Container containerExample = new Container();
        containerExample.setOriginId(originId);
        Example<Container> example = Example.of(containerExample, customExampleMatcher);

        // Execute query
        return this.findAll(example);
    }
}
