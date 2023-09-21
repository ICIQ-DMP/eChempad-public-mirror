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

import org.ICIQ.eChempad.configurations.security.ACL.AclServiceCustomImpl;
import org.ICIQ.eChempad.configurations.security.ACL.PermissionBuilder;
import org.ICIQ.eChempad.entities.genericJPAEntities.*;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.repositories.genericJPARepositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service("documentService")
public class DocumentServiceImpl<T extends Entity, S extends Serializable> extends GenericServiceImpl<Document, UUID> implements DocumentService<Document, UUID> {

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
        this.aclRepository.addPermissionToEntity(documentDB, true, PermissionBuilder.getFullPermissions(), null);

        return documentDB;
    }


    @Override
    public Set<Document> getDocumentsFromContainer(UUID container_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        // We punctually use the Entity manager to get a Journal entity from the Experiment service
        return super.entityManager.find(Container.class, container_uuid).getChildrenDocuments();
    }

    @Override
    public List<Document> searchByOriginId(String originId) {
        // Create matcher for the originId
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher("originId", ExampleMatcher.GenericPropertyMatchers.exact().caseSensitive());

        /*
         Create an empty container with the originId to match the entities with same originId from our database using
         the "example" query method
         */
        Document document = new Document();
        document.setOriginId(originId);
        Example<Document> example = Example.of(document, customExampleMatcher);

        // Execute query
        return this.findAll(example);    }
}
