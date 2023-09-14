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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

@Service("signalsImportService")
@ConfigurationProperties(prefix = "signals")
public class SignalsImportServiceImpl implements SignalsImportService {

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
    public void expandContainerChildren(Container dataEntity, String APIKey) {
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
    public boolean expandEntityChildren(DataEntity entity, String APIKey) {
        if (entity instanceof Document)
        {
            return false;
        }
        else
        {
            this.expandContainerChildren((Container) entity, APIKey);
            return true;
        }
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
    public void expandRootContainer(Container container, String APIKey) {
        // Expand experiment children
        this.expandContainerChildren(container, APIKey);

        // Expand documents
        for (Container experiment : container.getChildrenContainers())
        {
            this.expandContainerDocuments(experiment, APIKey);
        }
    }

    @Override
    public void expandContainerHierarchy(Container container, String APIKey) {}

    @Override
    public void updateRootContainer(Container container, String APIKey) {
        List<Container> containersMatching = this.containerService.searchByOriginId(container.getOriginId());
        if (containersMatching.size() != 0)
        {
            // Get first occurrence (There should be only one occurrence)
            Container containerMatching = containersMatching.get(0);
            UpdateState updateState = this.dataEntityUpdateStateService.compareEntities(containerMatching, container);

            Logger.getGlobal().warning("state:" + updateState.toString());
            if (updateState == UpdateState.ORIGIN_HAS_CHANGES)
            {
                /*
                Set the primary key of the matching data entity of our db to the data entity that we are importing,
                so Hibernate takes care of the update for us in the save method.
                */
                container.setId(containerMatching.getId());

                // Update db entity
                this.containerService.save(container);
            }
            else if (updateState == UpdateState.BOTH_HAVE_CHANGES)
            {
                /*
                We need to perform the travel algorithm through all the tree to determine the state of each
                sub-container.
                */


                // TODO
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
            // Save new data entity
            this.expandRootContainer(container, APIKey);
            this.containerService.save(container);
        }
    }

    @Override
    public void mergeEntities(Container parentInDatabase, DataEntity dataEntitySignals, String APIKey){
        // Query database for the data entities already present that hold the last state of the data entity from Signals
        List<? extends DataEntity> dataEntitiesMatching;
        if (dataEntitySignals instanceof Container)
        {
            dataEntitiesMatching = this.containerService.searchByOriginId(dataEntitySignals.getOriginId());
        }
        else
        {
            dataEntitiesMatching = this.documentService.searchByOriginId(dataEntitySignals.getOriginId());
        }

        if (dataEntitiesMatching.size() != 0)
        {
            // Get first occurrence (There should be only one occurrence)
            DataEntity dataEntity = dataEntitiesMatching.get(0);
            UpdateState updateState = this.dataEntityUpdateStateService.compareEntities(dataEntity, dataEntitySignals);

            Logger.getGlobal().warning("state:" + updateState.toString());
            if (updateState == UpdateState.ORIGIN_HAS_CHANGES)
            {
                /*
                Set the primary key of the matching data entity of our db to the data entity that we are importing,
                so Hibernate takes care of the update for us in the save method.
                */
                dataEntitySignals.setId(dataEntity.getId());

                // Update db entity
                this.containerService.save((Container) dataEntitySignals);
            }
            else if (updateState == UpdateState.BOTH_HAVE_CHANGES)
            {
                /*
                We need to perform the travel algorithm through all the tree to determine the state of each
                sub-container.
                */

                // TODO
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
                // Parent connect to the new subtree
                parentInDatabase.getChildrenContainers().add((Container) dataEntitySignals);

                this.containerService.save((Container) dataEntitySignals);
            }
            else if (dataEntitySignals instanceof Document)
            {
                // Build DocumentWrapper
                DocumentWrapper documentHelper = new DocumentWrapper((Document) dataEntitySignals);
                // Expand Document with Signals data
                this.expandDocumentFile(documentHelper, APIKey);
                // Connect to parent
                documentHelper.setParent(parentInDatabase);
                // Parent connect to the new subtree
                //parentInDatabase.getChildrenDocuments().add(documentHelper);

                this.containerService.save((Container) dataEntitySignals);
            }
        }
    }



    @Override
    public void importEntity(DataEntity dataEntity) {}

    @Override
    public String importWorkspace(String APIKey) {
        return null;
    }

    @Override
    public String importWorkspace() {
        return null;
    }

    @Override
    public String importJournal(String APIKey, Serializable id) {
        return null;
    }

    @Override
    public String importJournal(Serializable id) {
        return null;
    }


}
