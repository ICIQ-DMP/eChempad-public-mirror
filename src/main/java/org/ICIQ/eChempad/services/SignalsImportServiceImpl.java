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
import org.ICIQ.eChempad.configurations.wrappers.UserDetailsImpl;
import org.ICIQ.eChempad.entities.DocumentWrapper;
import org.ICIQ.eChempad.entities.UpdateState;
import org.ICIQ.eChempad.entities.genericJPAEntities.*;
import org.ICIQ.eChempad.services.genericJPAServices.ContainerService;
import org.ICIQ.eChempad.services.genericJPAServices.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
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

// https://stackoverflow.com/questions/38705890/what-is-the-difference-between-objectnode-and-jsonnode-in-jackson
@Service("signalsImportService")
@ConfigurationProperties(prefix = "signals")
public class SignalsImportServiceImpl implements SignalsImportService {

    /**
     * The base URL of the Signals API.
     */
    static final String baseURL = "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0";

    /**
     * If true determines that it should ignore all the resources from which the current user is not an owner. If false
     * import all accessible resources.
     */
    static final boolean getOnlyOwnedResources = false;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ContainerService<Container, UUID> containerService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private DocumentService<Document, UUID> documentService;

    @Autowired
    private DataEntityUpdateStateService dataEntityUpdateStateService;

    @Autowired
    private WebClient webClient;

    @Autowired
    private DocumentWrapperConverter documentWrapperConverter;

    public Date parseDateFromJSON(ObjectNode metadataJSON)
    {
        String signalsJournalCreationDate = metadataJSON.get("data").get(0).get("attributes").get("createdAt").toString().replace("\"", "");
        return this.parseDate(signalsJournalCreationDate);
    }

    public Date parseUpdateDateFromJSON(ObjectNode metadataJSON)
    {
        String signalsJournalCreationDate = metadataJSON.get("data").get(0).get("attributes").get("editedAt").toString().replace("\"", "");
        return this.parseDate(signalsJournalCreationDate);
    }

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

    public Container parseContainer(ObjectNode containerJSON)
    {
        // Create unmanaged container to save the metadata
        Container rootEntity = new Container();

        // Remove quotes and obtain the EID of this entity.
        rootEntity.setOriginId(containerJSON.get("data").get(0).get("id").toString().replace("\"", ""));

        // Parse root container name
        rootEntity.setName(containerJSON.get("data").get(0).get("attributes").get("name").toString().replace("\"", ""));

        // Parse root container description
        rootEntity.setDescription(containerJSON.get("data").get(0).get("attributes").get("description").toString().replace("\"", ""));

        // Parse root container creation date
        Date now = new Date();
        rootEntity.setCreationDate(now);

        // Parse root container creation date in origin
        rootEntity.setOriginCreationDate(this.parseDateFromJSON(containerJSON));

        // Set local last edition date with the last edition date in origin
        rootEntity.setLastEditionDate(now);

        // Parse root container last edition date in origin
        rootEntity.setOriginLastEditionDate(this.parseUpdateDateFromJSON(containerJSON));

        // Set origin platform
        rootEntity.setOriginPlatform("Signals");

        // Parse digest value
        rootEntity.setDigest(containerJSON.get("data").get(0).get("attributes").get("digest").toString().replace("\"", ""));

        // Parse entity type in origin
        rootEntity.setOriginType(containerJSON.get("data").get(0).get("attributes").get("type").toString().replace("\"", ""));

        // Parse department
        rootEntity.setDepartment(containerJSON.get("data").get(0).get("attributes").get("fields").get("Department").get("value").toString().replace("\"", ""));

        // Username of the owner in origin
        /*
         I found out that it seems that when a journal is not owned by you, you can not get the included information
         and because of that the common way of parsing the owner username does not work
         */
        if (containerJSON.get("included") == null)
        {
            rootEntity.setOriginOwnerUsername("unknown");
        }
        else
        {
            rootEntity.setOriginOwnerUsername(containerJSON.get("included").get(0).get("attributes").get("userName").toString().replace("\"", ""));
        }

        return rootEntity;
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
                Logger.getGlobal().warning("last edition date in read root entities " + rootEntity.getOriginLastEditionDate().toString());

                i++;

                rootEntities.add(rootEntity);
            }
        }
        return rootEntities;
    }

    @Override
    public void expandEntityChildren(Container dataEntity, String APIKey) {
        // ArrayNode experiments = this.objectMapper.createArrayNode();
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
                Logger.getGlobal().warning("username" + containerChildrenJSON.toPrettyString());

                // Create unmanaged container to save the metadata
                Container signalsExperiment = this.parseContainer(containerChildrenJSON);
                signalsExperiment.setParent(dataEntity);
                children.add(signalsExperiment);

                i++;
            }
        }
        dataEntity.setChildrenContainers(children);
    }

    public ObjectNode getChildFromContainer(String APIKey, int pageOffset, String container_eid)
    {
        return this.webClient.get()
                .uri(SignalsImportServiceImpl.baseURL + "/entities/" + container_eid + "/children?page[offset]=" + ((Integer) pageOffset).toString() + "&page[limit]=1&include=children%2C%20owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }



    @Override
    public void expandEntityHierarchy(DataEntity dataEntity, String APIKey) {

    }

    @Override
    public void importEntity(DataEntity dataEntity) {

    }

    @Override
    public void updateEntity(DataEntity dataEntity, String APIKey) {


    }

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
                this.containerService.save((Container) container);
            }
            else if (updateState == UpdateState.BOTH_HAVE_CHANGES)
            {
                /*
                We need to perform the travel algorithm through all the tree to determine the state of each
                sub-container.
                */


            }
            // updateState == UpdateState.UP_TO_DATE and updateState == UpdateState.ECHEMPAD_HAS_CHANGES do nothing
        }
        else  // NOT_PRESENT
        {
            // Save new data entity
            this.expandEntityChildren((Container) container, APIKey);
            this.containerService.save((Container) container);
        }
    }

    public String importWorkspace(String APIKey) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        this.getJournals(APIKey, stringBuilder);
        return stringBuilder.toString();
    }

    public String importWorkspace() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        String APIKey = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getResearcher().getSignalsAPIKey();

        this.getJournals(APIKey, stringBuilder);
        return stringBuilder.toString();
    }


    public ObjectNode getJournalWithEUID(String APIKey, String journalEUID)
    {
        return this.webClient.get()
                .uri(SignalsImportServiceImpl.baseURL + "/entities/" + journalEUID + "?include=owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    public ObjectNode getUserData(String APIKey, int userNumber)
    {
        return this.webClient.get()
                .uri(SignalsImportServiceImpl.baseURL + "/users/" + userNumber)
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    public String importJournal(Serializable id) {
        return this.importJournal(((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getResearcher().getSignalsAPIKey(), id);
    }

    @Override
    public String importJournal(String APIKey, Serializable id) {

        StringBuilder stringBuilder = new StringBuilder();
        ObjectNode journalJSON;

        journalJSON = this.getJournalWithEUID(APIKey, id.toString());

        // Check if the journal owner email coincides with the email of the logged user, if not discard journal
        JsonNode includedData = journalJSON.get("included");
        JsonNode includedOwner = includedData.get(0);
        JsonNode ownerAttributes = includedOwner.get("attributes");
        JsonNode ownerName = ownerAttributes.get("userName");
        String ownerNameTrimmed = ownerName.toString().replace("\"", "");

        if (getOnlyOwnedResources && ! ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getResearcher().getUsername().equals(ownerNameTrimmed))
        {
            return "Owner name does not coincide with currently logged user in eChempad application side" + ownerName + " with attributes "+ ownerAttributes.toString();
        }

        // Remove quotes and obtain the EID of this entity.
        String journal_eid = journalJSON.get("data").get("attributes").get("eid").toString().replace("\"", "");

        // Create unmanaged journal to save the metadata
        Container signalsContainer = new Container();

        // Parse and log journal name
        String signalsJournalName = journalJSON.get("data").get("attributes").get("name").toString().replace("\"", "");
        if (signalsJournalName.equals(""))
        {
            signalsJournalName = "(No name provided)";
        }
        signalsContainer.setName(signalsJournalName);
        stringBuilder.append(" * Journal ").append(0).append(" with EID ").append(journal_eid).append(": ").append(signalsJournalName).append("\n");

        // Parse journal description
        String signalsJournalDescription = journalJSON.get("data").get("attributes").get("description").toString().replace("\"", "");
        if (signalsJournalDescription.equals(""))
        {
            signalsJournalDescription = "(No description provided)";
        }
        signalsContainer.setDescription(signalsJournalDescription);

        // metadata parsing (...)

        this.containerService.save(signalsContainer);

        // Now call getExperimentsFromJournal using the created journal in order to import their children, recursively
        // This function will fill the passed journal with the new retrieved experiments from Signals. It will also
        // call the function to getDocumentFromExperiment passing the reference of the experiment, to fill the DB.
        this.getExperimentsFromJournal(APIKey, journal_eid, (UUID) signalsContainer.getId(), stringBuilder);
        return stringBuilder.toString();
    }


    public void getJournals(String APIKey, StringBuilder stringBuilder)
    {
        ObjectNode journalJSON;
        int i = 0;
        while ((journalJSON = this.getJournalWithOffset(APIKey, i)) != null)
        {
            // Iterate until the data of the entity is empty
            if (journalJSON.get("data").isEmpty())
            {
                break;
            }
            else
            {
                // Check if the journal owner email coincides with the email of the logged user, if not discard journal
                JsonNode includedData = journalJSON.get("included");
                JsonNode includedOwner = includedData.get(0);
                JsonNode ownerAttributes = includedOwner.get("attributes");
                JsonNode ownerName = ownerAttributes.get("userName");
                String ownerNameTrimmed = ownerName.toString().replace("\"", "");

                if (getOnlyOwnedResources && ! ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getResearcher().getUsername().equals(ownerNameTrimmed))
                {
                    // TODO throw unchecked ex
                    Logger.getGlobal().info("Owner name does not coincide with currently logged user in eChempad application side" + ownerName);

                    i++;
                    continue;
                }

                // Remove quotes and obtain the EID of this entity.
                String journal_eid = journalJSON.get("data").get(0).get("id").toString().replace("\"", "");

                // Create unmanaged journal to save the metadata
                Container signalsContainer = new Container();

                // Parse and log journal name
                String signalsJournalName = journalJSON.get("data").get(0).get("attributes").get("name").toString().replace("\"", "");
                if (signalsJournalName.equals(""))
                {
                    signalsJournalName = "(No name provided)";
                }
                signalsContainer.setName(signalsJournalName);
                stringBuilder.append(" * Journal ").append(i).append(" with EID ").append(journal_eid).append(": ").append(signalsJournalName).append("\n");

                // Parse journal description
                String signalsJournalDescription = journalJSON.get("data").get(0).get("attributes").get("description").toString().replace("\"", "");
                if (signalsJournalDescription.equals(""))
                {
                    signalsJournalDescription = "(No description provided)";
                }
                signalsContainer.setDescription(signalsJournalDescription);

                // Parse journal creation date
                signalsContainer.setCreationDate(this.parseDateFromJSON(journalJSON));

                // metadata parsing (...)
                this.containerService.save(signalsContainer);

                // Now call getExperimentsFromJournal using the created journal in order to import their children, recursively
                // This function will fill the passed journal with the new retrieved experiments from Signals. It will also
                // call the function to getDocumentFromExperiment passing the reference of the experiment, to fill the DB.
                this.getExperimentsFromJournal(APIKey, journal_eid, (UUID) signalsContainer.getId(), stringBuilder);
                i++;
            }
        }
    }

    public ObjectNode getJournalWithOffset(String APIKey, int pageOffset)
    {
        return this.webClient.get()
                .uri(SignalsImportServiceImpl.baseURL + "/entities?page[offset]=" + ((Integer) pageOffset).toString() + "&page[limit]=1&includeTypes=journal&include=owner") // &includeOptions=mine
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    public void getExperimentsFromJournal(String APIKey, String journal_eid, UUID journal_uuid, StringBuilder stringBuilder)
    {
        // ArrayNode experiments = this.objectMapper.createArrayNode();
        ObjectNode experimentJSON;
        int i = 0;
        while ((experimentJSON = this.getExperimentFromJournal(APIKey, i, journal_eid)) != null)
        {
            // Iterate until the data of the entity is empty
            if (experimentJSON.get("data").isEmpty())
            {
                break;
            }
            else
            {
                // Here we will call getDocuments, we will append each document into a list inside of
                // journal{data}[0]{relationships}{children} = []
                String experiment_eid = experimentJSON.get("data").get(0).get("id").toString().replace("\"", "");

                // Create unmanaged journal to save the metadata
                Container signalsExperiment = new Container();

                // Parse and log experiment name
                String signalsExperimentName = experimentJSON.get("data").get(0).get("attributes").get("name").toString().replace("\"", "");
                if (signalsExperimentName.equals(""))
                {
                    signalsExperimentName = "(No name provided)";
                }
                signalsExperiment.setName(signalsExperimentName);
                stringBuilder.append("   - Experiment ").append(i).append(" with EID ").append(experiment_eid).append(": ").append(signalsExperimentName).append("\n");

                // Parse experiment description
                String signalsExperimentDescription = experimentJSON.get("data").get(0).get("attributes").get("description").toString().replace("\"", "");
                if (signalsExperimentDescription.equals(""))
                {
                    signalsExperimentDescription = "(No description provided)";
                }
                signalsExperiment.setDescription(signalsExperimentDescription);

                // Parse experiment creation date
                signalsExperiment.setCreationDate(this.parseDateFromJSON(experimentJSON));

                // metadata parsing (...)

                Set<Container> experiments = new HashSet<Container>();
                experiments.add(signalsExperiment);
                this.containerService.addContainersToContainer(experiments, journal_uuid);

                // printJSON(experimentJSON);

                // Now call getExperimentsFromJournal using the created journal in order to import their children, recursively
                // This function will fill the passed journal with the new retrieved experiments from Signals. It will also
                // call the function to getDocumentFromExperiment passing the reference of the experiment, to fill the DB.
                this.getDocumentsFromExperiment(APIKey, experiment_eid, (UUID) signalsExperiment.getId(), stringBuilder);

                i++;
            }
        }
    }

    public ObjectNode getExperimentFromJournal(String APIKey, int pageOffset, String journal_eid)
    {
        return this.webClient.get()
                .uri(SignalsImportServiceImpl.baseURL + "/entities/" + journal_eid + "/children?page[offset]=" + ((Integer) pageOffset).toString() + "&page[limit]=1&include=children%2C%20owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    public void getDocumentsFromExperiment(String APIKey, String experiment_eid, UUID experiment_uuid, StringBuilder stringBuilder)
    {
        ObjectNode documentJSON;
        int i = 0;
        while ((documentJSON = this.getDocumentFromExperiment(APIKey, i, experiment_eid)) != null)
        {
            // Iterate until the data of the entity is empty
            if (documentJSON.get("data").isEmpty())
            {
                break;
            }
            else
            {
                // Parse Signals document
                String document_eid = documentJSON.get("data").get(0).get("id").toString().replace("\"", "");
                DocumentWrapper documentHelper = new DocumentWrapper();

                // Parse document name
                String documentHelperName = documentJSON.get("data").get(0).get("attributes").get("name").toString().replace("\"", "");
                if (documentHelperName.equals(""))
                {
                    documentHelperName = "(No name provided)";
                }
                documentHelper.setName(documentHelperName);

                // Parse document description
                String documentHelperDescription = documentJSON.get("data").get(0).get("attributes").get("description").toString().replace("\"", "");
                if (documentHelperDescription.equals(""))
                {
                    documentHelperDescription = "(No description provided)";
                }
                documentHelper.setDescription(documentHelperDescription);

                // Parse journal creation date
                documentHelper.setCreationDate(this.parseDateFromJSON(documentJSON));

                // Parse file
                // First we obtain the inputStream of this document, which actually corresponds to a file. We also need
                // to obtain the values of the HTTP header "Content-Disposition" which looks like this in an arbitrary
                // document export:
                //
                // attachment; filename="MZ7-085-DC_10%5B1%5D.zip"; filename*=utf-8''MZ7-085-DC_10%5B1%5D.zip
                //
                // We would need to obtain the data of the filename which is the filed with the name "filename" and in
                // the previous example has the value "MZ7-085-DC_10%5B1%5D.zip"
                // We also need to obtain the header Content-type which indicates the mimetype of the file we are
                // retrieving. That will allow to know the type of file inorder to process it further.
                HttpHeaders receivedHeaders = null;
                try {
                    receivedHeaders = new HttpHeaders();
                    InputStream is = this.exportDocument(APIKey, document_eid, receivedHeaders).getInputStream();
                    MultipartFile multipartFile = new MockMultipartFile(document_eid, receivedHeaders.getContentDisposition().getFilename(), receivedHeaders.getContentType().toString(), is);

                    documentHelper.setFile(multipartFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // File length is not needed to be parsed since it is conserved inside the bytestream

                // Debug printing
                stringBuilder.append("     # Document ").append(i).append(" with EID ").append(document_eid).append(": ").append(documentHelperName).append("\n");

                // Transform into a DB document
                Document document = this.documentWrapperConverter.convertToDatabaseColumn(documentHelper);

                // Add the parsed document to its corresponding experiment
                this.documentService.addDocumentToContainer(document, experiment_uuid);
                i++;
            }
        }
    }

    public ObjectNode getDocumentFromExperiment(String APIKey, int pageOffset, String experiment_eid)
    {
        return this.webClient.get()
                .uri(SignalsImportServiceImpl.baseURL + "/entities/" + experiment_eid + "/children?page[offset]=" + ((Integer) pageOffset) + "&page[limit]=1&include=children%2C%20owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    public ByteArrayResource exportDocument(String APIKey, String document_eid, HttpHeaders receivedHeaders) throws IOException {

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
}
