package org.ICIQ.eChempad.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ICIQ.eChempad.configurations.DocumentHelper;
import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.entities.Journal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.util.UUID;
import java.util.logging.Logger;

// https://stackoverflow.com/questions/38705890/what-is-the-difference-between-objectnode-and-jsonnode-in-jackson
@Service
@ConfigurationProperties(prefix = "signals")
public class SignalsImportServiceClass implements SignalsImportService {

    // https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0
    @Value("${signals.baseURL}")
    private String baseURL;

    private final ExperimentService experimentService;

    private final DocumentService documentService;

    private final JournalService journalService;

    private final WebClient webClient;

    private final SecurityService securityService;

    static void printJSON(ObjectNode objectNode)
    {
        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            System.out.println("JOURNAL JSON" + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public SignalsImportServiceClass(ExperimentService experimentService, DocumentService documentService, JournalService journalService, WebClient webClient, SecurityService securityService) {
        this.experimentService = experimentService;
        this.documentService = documentService;
        this.journalService = journalService;
        this.webClient = webClient;
        this.securityService = securityService;
    }


   /** public ByteArrayResource testexportDocument(String APIKey, String document_eid) throws IOException {

        final WebClient webClient = WebClient.create();

        String url = this.baseURL + "/entities/" + document_eid + "/export";

        Logger.getGlobal().info("\n\n\n the document expoding is: " + document_eid);

        return webClient.get()
                .uri(url)
                .header("x-api-key", APIKey)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .block();
    }*/


    public String importSignals(String APIKey) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IMPORTED RESOURCES FROM SIGNALS NOTEBOOK: \n");
        this.getJournals(APIKey, stringBuilder);
        return stringBuilder.toString();
    }

    public void getJournals(String APIKey, StringBuilder stringBuilder)
    {
        ObjectNode journalJSON;
        int i = 0;
        while ((journalJSON = this.getJournal(APIKey, i)) != null)
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
                Logger.getGlobal().info(ownerNameTrimmed);

                if (! this.securityService.getLoggedResearcher().getEmail().equals(ownerNameTrimmed))
                {
                    i++;
                    continue;
                }

                // Remove quotes and obtain the EID of this entity.
                String journal_eid = journalJSON.get("data").get(0).get("id").toString().replace("\"", "");

                // Create unmanaged journal to save the metadata
                Journal signalsJournal = new Journal();

                // Parse and log journal name
                String signalsJournalName = journalJSON.get("data").get(0).get("attributes").get("description").toString().replace("\"", "");
                if (signalsJournalName.equals(""))
                {
                    signalsJournalName = "(No name provided)";
                }
                signalsJournal.setName(signalsJournalName);
                stringBuilder.append(" * Journal ").append(i).append(" with EID ").append(journal_eid).append(": ").append(signalsJournalName).append("\n");

                // Parse journal description
                String signalsJournalDescription = journalJSON.get("data").get(0).get("attributes").get("name").toString().replace("\"", "");
                signalsJournal.setDescription(signalsJournalDescription);

                // metadata parsing (...)

                this.journalService.addJournal(signalsJournal);

                // Now call getExperimentsFromJournal using the created journal in order to import their children, recursively
                // This function will fill the passed journal with the new retrieved experiments from Signals. It will also
                // call the function to getDocumentFromExperiment passing the reference of the experiment, to fill the DB.
                this.getExperimentsFromJournal(APIKey, journal_eid, signalsJournal.getUUid(), stringBuilder);
                i++;
                Logger.getGlobal().info("JOURNAL NUMBER " + i + " FINISHED IMPORT");
            }
        }
    }

    public ObjectNode getJournal(String APIKey, int pageOffset)
    {
        return this.webClient.get()
                .uri(this.baseURL + "/entities?page[offset]=" + ((Integer) pageOffset).toString() + "&page[limit]=1&includeTypes=journal&include=owner&includeOptions=mine")
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
                System.out.println("EXPERIMENT EID IS: " + experiment_eid);

                // Create unmanaged journal to save the metadata
                Experiment signalsExperiment = new Experiment();

                // Parse and log experiment name
                String signalsExperimentName = experimentJSON.get("data").get(0).get("attributes").get("description").toString().replace("\"", "");
                if (signalsExperimentName.equals(""))
                {
                    signalsExperimentName = "(No name provided)";
                }
                signalsExperiment.setName(signalsExperimentName);
                stringBuilder.append("   - Experiment ").append(i).append(" with EID ").append(experiment_eid).append(": ").append(signalsExperimentName).append("\n");

                // Parse experiment description
                signalsExperiment.setDescription(experimentJSON.get("data").get(0).get("attributes").get("name").toString().replace("\"", ""));

                // metadata parsing (...)

                this.experimentService.addExperimentToJournal(signalsExperiment, journal_uuid);

                // printJSON(experimentJSON);

                // Now call getExperimentsFromJournal using the created journal in order to import their children, recursively
                // This function will fill the passed journal with the new retrieved experiments from Signals. It will also
                // call the function to getDocumentFromExperiment passing the reference of the experiment, to fill the DB.
                this.getDocumentsFromExperiment(APIKey, experiment_eid, signalsExperiment.getUUid(), stringBuilder);

                i++;
            }
        }
    }

    public ObjectNode getExperimentFromJournal(String APIKey, int pageOffset, String journal_eid)
    {
        return this.webClient.get()
                .uri(this.baseURL + "/entities/" + journal_eid + "/children?page[offset]=" + ((Integer) pageOffset).toString() + "&page[limit]=1&include=children%2C%20owner")
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
                DocumentHelper documentHelper = new DocumentHelper();

                // Parse document name
                String documentHelperName = documentJSON.get("data").get(0).get("attributes").get("description").toString().replace("\"", "");
                if (documentHelperName.equals(""))
                {
                    documentHelperName = "(No name provided)";
                }
                documentHelper.setName(documentHelperName);

                // Parse document description
                String documentHelperDescription = documentJSON.get("data").get(0).get("attributes").get("name").toString().replace("\"", "");
                if (documentHelperDescription.equals(""))
                {
                    documentHelperDescription = "(No description provided)";
                }
                documentHelper.setDescription(documentHelperDescription);

                // Parse file
                try {
                    documentHelper.setFile(new MockMultipartFile(document_eid, this.exportDocument(APIKey, document_eid).getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }



                stringBuilder.append("     # Document ").append(i).append(" with EID ").append(document_eid).append(": ").append(documentHelperName).append("\n");
                // TODO: recover original filename from HTTP header Content-Disposition and return it.
                // Content-Disposition=attachment; filename="MZ7-085-DC_10%5B1%5D.zip"; filename*=utf-8''MZ7-085-DC_10%5B1%5D.zip
                // TODO: recover mimetype from HTTP header Content-Type
                // TODO: recover fileSize from HTTP header Content-Length

                this.documentService.addDocumentToExperiment(documentHelper, experiment_uuid);
                i++;
            }
        }
    }

    public ObjectNode getDocumentFromExperiment(String APIKey, int pageOffset, String experiment_eid)
    {
        return this.webClient.get()
                .uri(this.baseURL + "/entities/" + experiment_eid + "/children?page[offset]=" + ((Integer) pageOffset).toString() + "&page[limit]=1&include=children%2C%20owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    public ByteArrayResource exportDocument(String APIKey, String document_eid) throws IOException {

        String url = this.baseURL + "/entities/" + document_eid + "/export";

        //Logger.getGlobal().info("\n\n\n the document EID is: " + document_eid);

        ByteArrayResource byteArrayResource = this.webClient.get()
                .uri(url)
                .header("x-api-key", APIKey)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .block();



        // In the cases where there is stored an empty file in Signals we receive a nullPointer instead of a ByteArrayResource empty
        if (byteArrayResource == null)
        {
            return new ByteArrayResource("".getBytes());
        }
        else
        {
            return byteArrayResource;
        }
    }
}
