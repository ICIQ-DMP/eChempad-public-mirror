package org.ICIQ.eChempad.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.*;

// https://stackoverflow.com/questions/38705890/what-is-the-difference-between-objectnode-and-jsonnode-in-jackson
@Service
@ConfigurationProperties(prefix = "signals")
public class SignalsImportServiceClass implements SignalsImportService {

    // https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0
    @Value("${signals.baseURL}")
    private String baseURL;

    @Autowired
    private ObjectMapper objectMapper;

    public void importSignals(String APIKey) throws IOException {

        ArrayNode responseSpec = this.getJournals(APIKey);


        // TODO test to try to download a document
        // text:f99e67cf-8288-48de-987f-bccc14da012c
        /*WebClient client = WebClient.create();
        ByteArrayResource byteArrayResource = client.get()
                .uri(this.baseURL + "/entities/imageResource:a8d10066-7186-465b-a21d-c56aa819426c/export")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .block();
        */
        //System.out.println(byteArrayResource.getByteArray().toString());
        //System.out.println(byteArrayResource);
    }

    public ArrayNode getJournals(String APIKey)
    {
        ArrayNode journals = this.objectMapper.createArrayNode();
        ObjectNode journal;
        int i = 0;
        while ((journal = this.getJournal(APIKey, i)) != null)
        {
            // Iterate until the data of the entity is empty
            if (journal.get("data").isEmpty())
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
                String journal_eid = journal.get("data").get(0).get("id").toString().replace("\"", "");
                System.out.println("THEJOURNALMARCA IS:" + journal_eid);

                journal.putPOJO("experiments", this.getExperimentsFromJournal(APIKey, journal_eid));

                journals.add(journal);
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

    public ArrayNode getExperimentsFromJournal(String APIKey, String journal_eid)
    {
        ArrayNode experiments = this.objectMapper.createArrayNode();
        ObjectNode experiment;
        int i = 0;
        while ((experiment = this.getExperimentFromJournal(APIKey, i, journal_eid)) != null)
        {
            // TODO test remove prints
            /*try {
                System.out.println("MARCA 3EXPERIMENT CONTENTS IS " + this.objectMapper.writeValueAsString(experiment));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }*/

            // Iterate until the data of the entity is empty
            if (experiment.get("data").isEmpty())
            {
                break;
            }
            else
            {
                // Here we will call getDocuments, we will append each document into a list inside of
                // journal{data}[0]{relationships}{children} = []
                String experiment_eid = experiment.get("data").get(0).get("id").toString().replace("\"", "");
                System.out.println("EXPERIMENTeid IS" + experiment_eid);

                experiment.putPOJO("documents", this.getDocumentsFromExperiment(APIKey, experiment_eid));
                experiments.add(experiment);

                i++;
            }
        }

        return experiments;
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

    public ArrayNode getDocumentsFromExperiment(String APIKey, String experiment_eid)
    {
        ArrayNode documents = this.objectMapper.createArrayNode();
        ObjectNode document;
        int i = 0;
        while ((document = this.getDocumentFromExperiment(APIKey, i, experiment_eid)) != null)
        {
            // Iterate until the data of the entity is empty
            if (document.get("data").isEmpty())
            {
                break;
            }
            else
            {
                // Here we will call getDocuments, we will append each document into a list inside of
                // journal{data}[0]{relationships}{children} = []
                String document_eid = document.get("data").get(0).get("id").toString().replace("\"", "");
                System.out.println("DOCUMENTeid IS" + document_eid);

                documents.add(document);
                ByteArrayResource byteArrayResource;
                if (this.exportDocument(APIKey, document_eid) != null)
                {
                    // TODO activating this line produces a null pointer that has not been originated in this line, so its origin its unknown :D
                    //this.exportDocument(APIKey, document_eid);
                }
                System.out.println("MARCA export finished of cocument");

                i++;
            }
        }
        return documents;
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

    public InputStream exportDocument(String APIKey, String document_eid) throws IOException {

        WebClient webClient = WebClient.create();
        /*return client.get()
                .uri(this.baseURL + "/entities/" + document_eid + "/export")
                .header("x-api-key", APIKey)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .exchangeToFlux();
*/

        String url = this.baseURL + "/entities/" + document_eid + "/export";
        PipedOutputStream osPipe = new PipedOutputStream();
        PipedInputStream isPipe = new PipedInputStream(osPipe);

        ClientResponse response = webClient.get().uri(url)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .exchange()
                .block();

        final int statusCode = response.rawStatusCode();
        // check HTTP status code, can throw exception if needed
        // ....

        Flux<DataBuffer> body = response.body(BodyExtractors.toDataBuffers())
                .doOnError(t -> {
                    log.error("Error reading body.", t);
                    // close pipe to force InputStream to error,
                    // otherwise the returned InputStream will hang forever if an error occurs
                    try(isPipe) {
                        //no-op
                    } catch (IOException ioe) {
                        log.error("Error closing streams", ioe);
                    }
                })
                .doFinally(s -> {
                    try(osPipe) {
                        //no-op
                    } catch (IOException ioe) {
                        log.error("Error closing streams", ioe);
                    }
                });

        DataBufferUtils.write(body, osPipe)
                .subscribe(DataBufferUtils.releaseConsumer());

        return isPipe;
    }

}
