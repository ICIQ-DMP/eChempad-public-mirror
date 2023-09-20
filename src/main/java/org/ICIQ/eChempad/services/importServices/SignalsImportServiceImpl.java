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
package org.ICIQ.eChempad.services.importServices;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ICIQ.eChempad.configurations.converters.DocumentWrapperConverter;
import org.ICIQ.eChempad.entities.DocumentWrapper;
import org.ICIQ.eChempad.entities.UpdateState;
import org.ICIQ.eChempad.entities.genericJPAEntities.*;
import org.ICIQ.eChempad.services.DataEntityUpdateStateService;
import org.ICIQ.eChempad.services.genericJPAServices.ContainerService;
import org.ICIQ.eChempad.services.genericJPAServices.DocumentService;
import org.hibernate.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service("signalsImportService")
@ConfigurationProperties(prefix = "signals")
public class SignalsImportServiceImpl extends ImportServiceImpl implements SignalsImportService {

    private final ContainerService<Container, UUID> containerService;

    private final DocumentService<Document, UUID> documentService;

    private final DataEntityUpdateStateService dataEntityUpdateStateService;

    private final DocumentWrapperConverter documentWrapperConverter;

    private final SignalsAPIService signalsAPIService;

    private final SignalsAPIParsingService signalsAPIParsingService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public SignalsImportServiceImpl(ContainerService<Container, UUID> containerService, DocumentService<Document, UUID> documentService, DataEntityUpdateStateService dataEntityUpdateStateService, DocumentWrapperConverter documentWrapperConverter, SignalsAPIService signalsAPIService, SignalsAPIParsingService signalsAPIParsingService) {
        this.containerService = containerService;
        this.documentService = documentService;
        this.dataEntityUpdateStateService = dataEntityUpdateStateService;
        this.documentWrapperConverter = documentWrapperConverter;
        this.signalsAPIService = signalsAPIService;
        this.signalsAPIParsingService = signalsAPIParsingService;
    }

    @Override
    public List<DataEntity> readRootEntities(String APIKey) {
        List<DataEntity> rootEntities = new ArrayList<>();
        ObjectNode rootJSON;
        int i = 0;
        while ((rootJSON = this.signalsAPIService.getJournalWithOffset(APIKey, i)) != null)
        {
            // Iterate until the data of the entity is empty
            if (rootJSON.get("data").isEmpty())
            {
                break;
            }
            else  // If you find data parse it
            {
                Container rootEntity = this.signalsAPIParsingService.parseContainer(rootJSON);
                i++;
                rootEntities.add(rootEntity);
            }
        }
        return rootEntities;
    }

    @Override
    public void expandDocumentFile(DocumentWrapper documentWrapper, String APIKey)
    {
        HttpHeaders receivedHeaders;
        try {
            receivedHeaders = new HttpHeaders();
            InputStream is = this.signalsAPIService.exportDocumentFile(APIKey, documentWrapper.getOriginId(), receivedHeaders).getInputStream();
            MultipartFile multipartFile = this.signalsAPIParsingService.parseDocumentExport(documentWrapper.getOriginId(), receivedHeaders, is);
            documentWrapper.setFile(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void expandContainerContainers(Container dataEntity, String APIKey) {
        ObjectNode containerChildrenJSON;
        int i = 0;
        Set<Container> children = new HashSet<>();
        while ((containerChildrenJSON = this.signalsAPIService.getChildFromContainer(APIKey, i, dataEntity.getOriginId())) != null)
        {
            // Iterate until the data of the entity is empty
            if (containerChildrenJSON.get("data").isEmpty())
            {
                break;
            }
            else
            {
                // Create unmanaged container to save the metadata
                Container signalsContainer = this.signalsAPIParsingService.parseContainer(containerChildrenJSON);
                signalsContainer.setParent(dataEntity);
                children.add(signalsContainer);
                i++;
            }
        }
        dataEntity.setChildrenContainers(children);
    }

    @Override
    public void expandContainerDocuments(Container container, String APIKey)
    {
        ObjectNode documentJSON;
        int i = 0;
        while ((documentJSON = this.signalsAPIService.getChildFromContainer(APIKey, i, container.getOriginId())) != null)
        {
            // Iterate until the data of7 the entity is empty
            if (documentJSON.get("data").isEmpty())
            {
                break;
            }
            else
            {
                // Parse Signals document and put it inside a documentHelper
                DocumentWrapper documentHelper = new DocumentWrapper(this.signalsAPIParsingService.parseDocument(documentJSON));

                this.expandDocumentFile(documentHelper, APIKey);

                // Transform into a DB document
                Document document = this.documentWrapperConverter.convertToDatabaseColumn(documentHelper);

                // Link the document to its container
                document.setParent(container);
                container.getChildrenDocuments().add(document);

                i++;
            }
        }
    }

    @Override
    public boolean expandEntityChildren(DataEntity entity, String APIKey) {
        if (entity instanceof Document)
        {
            return false;
        }
        else
        {
            this.expandContainerContainers((Container) entity, APIKey);
            this.expandContainerDocuments((Container) entity, APIKey);
            return true;
        }
    }

    @Override
    public void expandContainerHierarchy(Container container, String APIKey) {
        this.expandEntityChildren(container, APIKey);
        for (Container containerChild: container.getChildrenContainers())
        {
            this.expandContainerHierarchy(containerChild, APIKey);
        }
    }

    @Override
    public void updateRootContainer(Container container, String APIKey) {
        this.syncEntity(null, container, APIKey);
    }

    /**
     * Flattens a list by returning the first element. This function is used statically by getSyncEntity.
     *
     * @param entities List of entities to flatten out.
     * @return The DataEntity matched.
     * @throws DuplicateKeyException Thrown exception if there is more than one match, which means there is more than
     * one entity with the same originId.
     */
    private DataEntity getSyncEntityPrivateFlattenList(List<? extends DataEntity> entities) throws DuplicateKeyException
    {
        if (entities.size() != 0)
        {
            // The corresponding root container already exists in the DB
            if (entities.size() == 1)
            {
                return entities.get(0);
            }
            else
            {
                Logger.getGlobal().warning("WARNING: matched more than one element with origin ID ");
                throw new DuplicateKeyException("Duplicated key in origin ID");
            }
        }
        else
        {
            // The corresponding root container does not exist in the DB
            return null;
        }
    }

    /**
     * Returns the first matching entity in our database with the same originId as the supplied dataEntitySignals. This
     * last variable is expected to be unmanaged by hibernate. Returns null if there is no match.
     *
     * @param parentInDatabase Parent in the database. This is the entity that we are querying for a match of its
     *                         children.
     * @param dataEntitySignals Data parsed from Signals not saved in the database (unmanaged)
     * @return The managed DataEntity in our database with the same originId as the dataEntitySignals data supplied by
     * parameter.
     */
    private DataEntity getSyncedEntity(Container parentInDatabase, DataEntity dataEntitySignals)
    {
        /*
        Query database for the root containers that may already be present holding the last state of the data entity
        from Signals.
        */
        if (parentInDatabase == null) {
            // No parent supplied, this is supposed to be a root container
            List<Container> rootContainersMatching;
            rootContainersMatching = this.containerService.searchByOriginId(dataEntitySignals.getOriginId());
            return this.getSyncEntityPrivateFlattenList(rootContainersMatching);
        }
        else  // Not a root container, query the children of the parent in DB for match
        {
            // The data entity to match is a container, query container for matches
            if (dataEntitySignals instanceof Container)
            {
                Set<Container> childrenContainerFromParentInDatabase = parentInDatabase.getChildrenContainers();
                List<Container> matchingContainers = childrenContainerFromParentInDatabase.stream().filter(
                        (Container container) ->
                        {
                            return container.getOriginId().equals(dataEntitySignals.getOriginId());
                        }
                ).collect(Collectors.toList());
                return this.getSyncEntityPrivateFlattenList(matchingContainers);
            }
            else if (dataEntitySignals instanceof Document)
            {
                Set<Document> childrenDocumentFromParentInDatabase = parentInDatabase.getChildrenDocuments();
                List<Document> matchingDocuments = childrenDocumentFromParentInDatabase.stream().filter(
                        (Document document) ->
                        {
                            return document.getOriginId().equals(dataEntitySignals.getOriginId());
                        }
                ).collect(Collectors.toList());
                return this.getSyncEntityPrivateFlattenList(matchingDocuments);
            }
            else
            {
                Logger.getGlobal().warning("Received unknown type in syncEntity");
                throw new TypeMismatchException("unknown type\n");
            }
        }
    }

    public Date computeLastEditionDate(DataEntity dataEntity, String APIKey)
    {
        return this.computeLastEditionDate(dataEntity, dataEntity.getLastEditionDate(), APIKey);
    }

    /**
     * Receives a data entity and computes its real last edition date and returns it if it is more recent than the date
     * received by parameter. If the entity has children, they are expanded and this same function is used to compute
     * its last edition date. Finally, the most recent last edition date among all the children and the received
     * parameter is what the function returns. If the received entity is a document no recursion calls are made and the
     * returned date is the most recent date between the document and the received date.
     *
     * @param dataEntity DataEntity that we are querying in this recursive step.
     * @param date Tail recursion data.
     * @return The most recent date between the supplied date and the date of the entity or its children if present.
     */
    private Date computeLastEditionDate(DataEntity dataEntity, Date date, String APIKey)
    {
        // Choose most recent date between current entity and supplied
        Date tail;
        if (this.dataEntityUpdateStateService.compareDates(dataEntity.getOriginLastEditionDate(), date) > 0)
        {
            tail = dataEntity.getOriginLastEditionDate();
        }
        else
        {
            tail = date;
        }

        // Because of the implementation of the behaviour of the field "editedAt" in Signals, we are forced to get
        // metadata of all tree to ensure that we are getting the last edition date. it does not matter that we have a
        // supplied date higher than the lastEditionDate of the received entity.

        // Expand children
        this.expandEntityChildren(dataEntity, APIKey);

        if (dataEntity instanceof Document)
        {
            // Base case, no recursive call
            return tail;
        }
        else if (dataEntity instanceof Container)
        {
            for (DataEntity child: ((Container) dataEntity).getChildrenDocuments()) {
                tail = this.computeLastEditionDate(child, tail, APIKey);
            }

            for (DataEntity child: ((Container) dataEntity).getChildrenContainers()) {
                tail = this.computeLastEditionDate(child, tail, APIKey);
            }
        }
        else
        {
            Logger.getGlobal().warning("Data entity is of unknown type " + dataEntity.getTypeName());
        }
        return tail;
    }

    @Override
    public void syncEntity(Container parentInDatabase, DataEntity dataEntitySignals, String APIKey) {
        DataEntity correspondingDatabaseDataEntity = this.getSyncedEntity(parentInDatabase, dataEntitySignals);
        // Matched entity
        if (correspondingDatabaseDataEntity != null)
        {
            Logger.getGlobal().info("Computing date of " + dataEntitySignals);
            Date lastEditionDate = this.computeLastEditionDate(dataEntitySignals, APIKey);
            dataEntitySignals.setOriginLastEditionDate(lastEditionDate);
            UpdateState updateState = this.dataEntityUpdateStateService.compareEntities(correspondingDatabaseDataEntity, dataEntitySignals);
            Logger.getGlobal().warning("state:" + updateState.toString());
            if (updateState == UpdateState.ORIGIN_HAS_CHANGES)
            {
                /*
                Set the primary key of the matching data entity of our db to the data entity that we are importing,
                so Hibernate takes care of the update for us in the save method.
                */
                dataEntitySignals.setId(correspondingDatabaseDataEntity.getId());

                // Update db entity
                this.containerService.save((Container) dataEntitySignals);
            }
            else if (updateState == UpdateState.BOTH_HAVE_CHANGES)
            {
                /*
                We need to perform the travel algorithm through all the tree to determine the state of each
                sub-container.
                */

                // 1 Expand children entities in dataEntitySignals
                this.expandEntityChildren(dataEntitySignals, APIKey);
                // 2 For each child propagate recursive call with its own parent
                if (dataEntitySignals instanceof Container)
                {
                    /*
                     If it is a container, we need to know if it is a terminal container (container that contains
                     documents) or a regular container, which is a container that contains other containers. This is
                     what the Signals specification tells us to do. But to give more flexibility we will check all cases
                     at all times, so we can sync entities that do not have these limitations in its structure.
                     */
                    for (Document document: ((Container) dataEntitySignals).getChildrenDocuments())
                    {
                        // Propagate call
                        this.syncEntity((Container) dataEntitySignals, document, APIKey);
                    }
                    for (Container container: ((Container) dataEntitySignals).getChildrenContainers())
                    {
                        // Propagate call
                        this.syncEntity((Container) dataEntitySignals, container, APIKey);
                    }
                }
                else if (dataEntitySignals instanceof Document)
                {
                    /*
                     If the dataEntitySignals is a Document already present in the DB we do not need to do anything.
                     This is actually an irreconcilable change
                     */
                    Logger.getGlobal().warning("In sync entity two document are found to have irreconcilable " +
                            "changes");
                }
                else
                {
                    throw new TypeMismatchException("Received non-controlled type in sync entity");
                }
            }
            else if (updateState == UpdateState.UP_TO_DATE)
            {
                Logger.getGlobal().warning("Entity is up to date, ignoring");
            }
            else if (updateState == UpdateState.ECHEMPAD_HAS_CHANGES)
            {
                Logger.getGlobal().warning("echempad has changes in the entity, ignoring import data");
            }
        }
        else  // NOT_PRESENT
        {
            // Save new data entity that is a container
            if (dataEntitySignals instanceof Container) {
                // Expand container with Signals data
                this.expandContainerHierarchy((Container) dataEntitySignals, APIKey);
                // Connect to parent
                dataEntitySignals.setParent(parentInDatabase);
                if (parentInDatabase != null)  // Set children of parent if available
                {
                    // Parent connects to the new subtree
                    parentInDatabase.getChildrenContainers().add((Container) dataEntitySignals);
                }
                // Save to database
                this.containerService.save((Container) dataEntitySignals);
            }
            else if (dataEntitySignals instanceof Document)
            {
                // Build DocumentWrapper
                DocumentWrapper documentHelper = new DocumentWrapper((Document) dataEntitySignals);
                // Expand Document with Signals data
                this.expandDocumentFile(documentHelper, APIKey);
                // Convert to Document back
                Document expanded = this.documentWrapperConverter.convertToDatabaseColumn(documentHelper);
                // Connect to parent
                expanded.setParent(parentInDatabase);
                if (parentInDatabase != null)  // Set children of parent if available
                {
                    // Parent connects to the new subtree
                    parentInDatabase.getChildrenDocuments().add(expanded);
                }
                // Save to database
                this.documentService.save((Document) dataEntitySignals);
            }
        }
    }
}
