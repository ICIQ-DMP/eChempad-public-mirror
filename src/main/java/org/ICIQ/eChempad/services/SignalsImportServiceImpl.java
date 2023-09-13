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
package org.ICIQ.eChempad.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ICIQ.eChempad.configurations.converters.DocumentWrapperConverter;
import org.ICIQ.eChempad.entities.DocumentWrapper;
import org.ICIQ.eChempad.entities.UpdateState;
import org.ICIQ.eChempad.entities.genericJPAEntities.*;
import org.ICIQ.eChempad.services.genericJPAServices.ContainerService;
import org.ICIQ.eChempad.services.genericJPAServices.DocumentService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Service("signalsImportService")
@ConfigurationProperties(prefix = "signals")
public class SignalsImportServiceImpl implements SignalsImportService {

    /**
     * The base URL of the Signals API.
     */
    static final String baseURL = "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0";

    private final ContainerService<Container, UUID> containerService;

    private final DocumentService<Document, UUID> documentService;

    private final DataEntityUpdateStateService dataEntityUpdateStateService;

    private final WebClient webClient;

    private final DocumentWrapperConverter documentWrapperConverter;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SignalsImportServiceImpl(ContainerService<Container, UUID> containerService, DocumentService<Document, UUID> documentService, DataEntityUpdateStateService dataEntityUpdateStateService, WebClient webClient, DocumentWrapperConverter documentWrapperConverter) {
        this.containerService = containerService;
        this.documentService = documentService;
        this.dataEntityUpdateStateService = dataEntityUpdateStateService;
        this.webClient = webClient;
        this.documentWrapperConverter = documentWrapperConverter;
    }

    @Override
    public Date parseDateFromJSON(ObjectNode metadataJSON)
    {
        String signalsJournalCreationDate = metadataJSON.get("data").get(0).get("attributes").get("createdAt").toString().replace("\"", "");
        return this.parseDate(signalsJournalCreationDate);
    }

    @Override
    public Date parseUpdateDateFromJSON(ObjectNode metadataJSON)
    {
        String signalsJournalCreationDate = metadataJSON.get("data").get(0).get("attributes").get("editedAt").toString().replace("\"", "");
        return this.parseDate(signalsJournalCreationDate);
    }

    @Override
    public Date parseDate(String dateData)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = dateFormat.parse(dateData);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public Container parseContainer(ObjectNode containerJSON)
    {
        return new Container(this.parseDataEntity(containerJSON));
    }

    @Override
    public Document parseDocument(ObjectNode documentJSON) {
        return new Document(this.parseDataEntity(documentJSON));
    }

    @Override
    public DataEntity parseDataEntity(ObjectNode dataEntityJSON) {
        DataEntity dataEntity = new DataEntityImpl() {
            @Override
            public <T extends Entity> Class<T> getType() {
                return (Class<T>) DataEntityImpl.class;
            }
        };

        // Remove quotes and obtain the EID of this entity.
        dataEntity.setOriginId(dataEntityJSON.get("data").get(0).get("id").toString().replace("\"", ""));

        // Parse root container name
        dataEntity.setName(dataEntityJSON.get("data").get(0).get("attributes").get("name").toString().replace("\"", ""));

        // Parse root container description
        dataEntity.setDescription(dataEntityJSON.get("data").get(0).get("attributes").get("description").toString().replace("\"", ""));

        // Parse root container creation date
        Date now = new Date();
        dataEntity.setCreationDate(now);

        // Parse root container creation date in origin
        dataEntity.setOriginCreationDate(this.parseDateFromJSON(dataEntityJSON));

        // Set local last edition date with the last edition date in origin
        dataEntity.setLastEditionDate(now);

        // Get origin type
        dataEntity.setOriginType(dataEntityJSON.get("data").get(0).get("attributes").get("type").toString().replace("\"", ""));

        // Parse root container last edition date in origin
        dataEntity.setOriginLastEditionDate(this.parseUpdateDateFromJSON(dataEntityJSON));

        // Set origin platform
        dataEntity.setOriginPlatform("Signals");

        // Parse digest value
        dataEntity.setDigest(dataEntityJSON.get("data").get(0).get("attributes").get("digest").toString().replace("\"", ""));

        // Parse entity type in origin
        dataEntity.setOriginType(dataEntityJSON.get("data").get(0).get("attributes").get("type").toString().replace("\"", ""));

        // Parse department
        JsonNode objectNode = dataEntityJSON.get("data").get(0).get("attributes").get("fields").get("Department");
        // If we do not have the department field set it as unknown
        if (objectNode == null)
        {
            dataEntity.setDepartment("Unknown");
        }
        else
        {
            dataEntity.setDepartment(objectNode.get("value").toString().replace("\"", ""));
        }

        // Username of the owner in origin
        /*
         I found out that it seems that when a journal is not owned by you, you can not get the included information
         and because of that the common way of parsing the owner username does not work
         */
        if (dataEntityJSON.get("included") == null)
        {
            dataEntity.setOriginOwnerUsername("Unknown");
        }
        else
        {
            dataEntity.setOriginOwnerUsername(dataEntityJSON.get("included").get(0).get("attributes").get("userName").toString().replace("\"", ""));
        }
        return dataEntity;
    }

    @Override
    public ByteArrayResource exportDocumentFile(String APIKey, String document_eid, HttpHeaders receivedHeaders) throws IOException {

        String url = SignalsImportServiceImpl.baseURL + "/entities/" + document_eid + "/export";

        ResponseEntity<ByteArrayResource> responseEntity = this.webClient.get()
                .uri(url)
                .header("x-api-key", APIKey)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .toEntity(ByteArrayResource.class)
                .block();

        // We are using the headers parameter of the function as an output parameter. But remember that in Java
        // everything is a pointer but args in functions are read-only. So we can modify the inside of the object but
        // not make the pointer go to another location by pointing to another object.
        for (String headerKey: responseEntity.getHeaders().keySet())
        {
            receivedHeaders.put(headerKey, Objects.requireNonNull(responseEntity.getHeaders().get(headerKey)));
        }

        // In the cases where there is stored an empty file in Signals we receive a nullPointer instead of a ByteArrayResource empty
        if (responseEntity.getBody() == null)
        {
            return new ByteArrayResource("".getBytes());
        }
        else
        {
            return responseEntity.getBody();
        }
    }



    @Override
    public ObjectNode getEntityWithEUID(String APIKey, String journalEUID)
    {
        return this.webClient.get()
                .uri(SignalsImportServiceImpl.baseURL + "/entities/" + journalEUID + "?include=owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }



    @Override
    public ObjectNode getJournalWithOffset(String APIKey, int pageOffset)
    {
        return this.webClient.get()
                .uri(SignalsImportServiceImpl.baseURL + "/entities?page[offset]=" + pageOffset + "&page[limit]=1&includeTypes=journal&include=owner") // &includeOptions=mine
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    public ObjectNode getChildFromContainer(String APIKey, int pageOffset, String container_eid)
    {
        return this.webClient.get()
                .uri(SignalsImportServiceImpl.baseURL + "/entities/" + container_eid + "/children?page[offset]=" + ((Integer) pageOffset) + "&page[limit]=1&include=children%2C%20owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    @Override
    public List<DataEntity> readRootEntities(String APIKey) {
        List<DataEntity> rootEntities = new ArrayList<>();
        ObjectNode rootJSON;
        int i = 0;
        while ((rootJSON = this.getJournalWithOffset(APIKey, i)) != null)
        {
            // Iterate until the data of the entity is empty
            if (rootJSON.get("data").isEmpty())
            {
                break;
            }
            else  // If you find data parse it
            {
                Container rootEntity = this.parseContainer(rootJSON);
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
            InputStream is = this.exportDocumentFile(APIKey, documentWrapper.getOriginId(), receivedHeaders).getInputStream();
            MultipartFile multipartFile = new MockMultipartFile(documentWrapper.getOriginId(), receivedHeaders.getContentDisposition().getFilename(), receivedHeaders.getContentType().toString(), is);

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
        while ((containerChildrenJSON = this.getChildFromContainer(APIKey, i, dataEntity.getOriginId())) != null)
        {
            // Iterate until the data of the entity is empty
            if (containerChildrenJSON.get("data").isEmpty())
            {
                break;
            }
            else
            {
                // Create unmanaged container to save the metadata
                Container signalsContainer = this.parseContainer(containerChildrenJSON);
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
        while ((documentJSON = this.getChildFromContainer(APIKey, i, container.getOriginId())) != null)
        {
            // Iterate until the data of the entity is empty
            if (documentJSON.get("data").isEmpty())
            {
                break;
            }
            else
            {
                Logger.getGlobal().warning(documentJSON.toPrettyString());

                // Parse Signals document and put it inside a documentHelper
                DocumentWrapper documentHelper = new DocumentWrapper(this.parseDocument(documentJSON));

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
                parentInDatabase.getChildrenDocuments().add(documentHelper);

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


    // OLD METHODS
    @Override
    public ObjectNode getDocumentFromExperiment(String APIKey, int pageOffset, String experiment_eid)
    {
        return this.webClient.get()
                .uri(SignalsImportServiceImpl.baseURL + "/entities/" + experiment_eid + "/children?page[offset]=" + pageOffset + "&page[limit]=1&include=children%2C%20owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    @Override
    public ObjectNode getUserData(String APIKey, int userNumber)
    {
        return this.webClient.get()
                .uri(SignalsImportServiceImpl.baseURL + "/users/" + userNumber)
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

}
