package org.ICIQ.eChempad.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ICIQ.eChempad.configurations.DocumentHelper;
import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.entities.Journal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.Arrays;
import java.util.UUID;

// https://stackoverflow.com/questions/38705890/what-is-the-difference-between-objectnode-and-jsonnode-in-jackson
@Service
@ConfigurationProperties(prefix = "signals")
public class SignalsImportServiceClass implements SignalsImportService {

    // https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0
    @Value("${signals.baseURL}")
    private String baseURL;

    private final ObjectMapper objectMapper;

    private final ExperimentService experimentService;

    private final DocumentService documentService;

    private final JournalService journalService;


    public SignalsImportServiceClass(ObjectMapper objectMapper, ExperimentService experimentService, DocumentService documentService, JournalService journalService) {
        this.objectMapper = objectMapper;
        this.experimentService = experimentService;
        this.documentService = documentService;
        this.journalService = journalService;
    }

    public void importSignals(String APIKey) throws IOException {
        ArrayNode responseSpec = this.getJournals(APIKey);
    }

    public ArrayNode getJournals(String APIKey)
    {
        ArrayNode journals = this.objectMapper.createArrayNode();
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
                // Here we will call getExperiments and we will append each experiments into a list inside of
                // journal{data}[0]{relationships}{children} = []
                // get("data").get(9).get("relationships").get("children").
                // Remove quotes and the prefix followed by ":", that indicates the entity we are retrieving. If not, is
                // not a valid identifier for Signals API
                String journal_eid = journalJSON.get("data").get(0).get("id").toString().replace("\"", "");
                System.out.println("JOURNAL EID IS: " + journal_eid);

                try {
                    System.out.println("JOURNAL JSON" + this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(journalJSON));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                // Create unmanaged journal to save the metadata
                Journal signalsJournal = new Journal();
                signalsJournal.setName(journalJSON.get("data").get(0).get("attributes").get("description").toString());
                signalsJournal.setDescription(journalJSON.get("data").get(0).get("attributes").get("name").toString());
                // (...)

                this.journalService.addJournal(signalsJournal);

                // Now call getExperimentsFromJournal using the created journal in order to import their children, recursively
                // This function will fill the passed journal with the new retrieved experiments from Signals. It will also
                // call the function to getDocumentFromExperiment passing the reference of the experiment, to fill the DB.
                this.getExperimentsFromJournal(APIKey, journal_eid, signalsJournal.getUUid());

                // Add to the JSON of the journal
                //journalJSON.putPOJO("experiments", this.getExperimentsFromJournal(APIKey, journal_eid, signalsJournal.getUUid()));
                //journals.add(journalJSON);
                i++;
            }
        }

        try {
            System.out.println(this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(journals));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return journals;
    }

    public ObjectNode getJournal(String APIKey, int pageOffset)
    {
        // Map<Object, Object> URL_variables = Collections.emptyMap();
        WebClient client = WebClient.create();
        return client.get()
                .uri(this.baseURL + "/entities?page[offset]=" + ((Integer) pageOffset).toString() + "&page[limit]=1&includeTypes=journal&include=children%2C%20owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    public void getExperimentsFromJournal(String APIKey, String journal_eid, UUID journal_uuid)
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
                signalsExperiment.setName(experimentJSON.get("data").get(0).get("attributes").get("description").toString());
                signalsExperiment.setDescription(experimentJSON.get("data").get(0).get("attributes").get("name").toString());
                // (...)
                this.experimentService.addExperimentToJournal(signalsExperiment, journal_uuid);


                // Now call getExperimentsFromJournal using the created journal in order to import their children, recursively
                // This function will fill the passed journal with the new retrieved experiments from Signals. It will also
                // call the function to getDocumentFromExperiment passing the reference of the experiment, to fill the DB.
                this.getDocumentsFromExperiment(APIKey, experiment_eid, signalsExperiment.getUUid());

                i++;
            }
        }
    }

    public ObjectNode getExperimentFromJournal(String APIKey, int pageOffset, String journal_eid)
    {
        WebClient client = WebClient.create();
        return client.get()
                .uri(this.baseURL + "/entities/" + journal_eid + "/children?page[offset]=" + ((Integer) pageOffset).toString() + "&page[limit]=1&include=children%2C%20owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    public void getDocumentsFromExperiment(String APIKey, String experiment_eid, UUID experiment_uuid)
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
                String document_eid = documentJSON.get("data").get(0).get("id").toString().replace("\"", "");
                System.out.println("DOCUMENT EID IS: " + document_eid);
                System.out.println("DOCUMENT JSON CONTENT IS: " + documentJSON);

                DocumentHelper documentHelper = new DocumentHelper();
                documentHelper.setName(documentJSON.get("data").get(0).get("attributes").get("description").toString());
                documentHelper.setDescription(documentJSON.get("data").get(0).get("attributes").get("name").toString());
                /**try {
                    documentHelper.setFile(((MultipartFile) this.exportDocument(APIKey, document_eid)));
                } catch (IOException e) {
                    e.printStackTrace();
                }**/
                try {

                    //ByteArrayResource byteArrayResource = this.exportDocument(APIKey, document_eid);
                    //System.out.println(Arrays.toString(byteArrayResource.getByteArray()));



                    // This line explodes with null pointer because the multipartfile obtained from the body has a null
                    // input stream, so it fails when trying to save it.
                    documentHelper.setFile(new MockMultipartFile(document_eid, this.exportDocument(APIKey, document_eid).getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // (...)
                this.documentService.addDocumentToExperiment(documentHelper, experiment_uuid);

                // Stop recursion calls

                i++;
            }
        }
        // return documents;
    }

    public ObjectNode getDocumentFromExperiment(String APIKey, int pageOffset, String experiment_eid)
    {
        WebClient client = WebClient.create();
        return client.get()
                .uri(this.baseURL + "/entities/" + experiment_eid + "/children?page[offset]=" + ((Integer) pageOffset).toString() + "&page[limit]=1&include=children%2C%20owner")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    public ByteArrayResource exportDocument(String APIKey, String document_eid) throws IOException {

        final WebClient webClient = WebClient.create();

        String url = this.baseURL + "/entities/" + document_eid + "/export";

        return webClient.get()
                .uri(url)
                .header("x-api-key", APIKey)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .block();
    }



}
