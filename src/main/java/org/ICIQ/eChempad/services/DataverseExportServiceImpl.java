package org.ICIQ.eChempad.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.services.genericJPAServices.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of class to export data to a running Dataverse instance.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 24/10/2022
 * @see <a href="https://guides.dataverse.org/en/latest/api/intro.html">...</a>
 */
@Service
public class DataverseExportServiceImpl implements DataverseExportService {

    private static final String baseURL = "https://dataverse.csuc.cat/api";

    @Autowired
    JournalService<Journal, UUID> journalService;

    @Autowired
    private WebClient webClient;

    /**
     * To create a dataset, you must supply a JSON file that contains at least the following required metadata fields:
     * Title, Author, Contact, Description and Subject.
     *
     * @see <a href="https://stackoverflow.com/questions/30997362/how-to-modify-jsonnode-in-java">...</a>
     * @see <a href="https://guides.dataverse.org/en/5.10.1/api/native-api.html#create-dataverse-api">...</a>
     * @param APIKey Contains a token to log into the associated third-party application.
     * @param id Identifies the journal to export to the third-party service.
     * @return Returns a message for the controller regarding the correctness of the operations.
     */
    @Override
    public String exportJournal(String APIKey, Serializable id) {
        Optional<Journal> exportJournal = this.journalService.findById((UUID) id);
        Journal journal;

        if (! exportJournal.isPresent())
        {
            return "Could not export the journal " + id + ". It could not be found for the current user.";
        }
        else
        {
            journal = exportJournal.get();
        }

        JSONArray fields = new JSONArray();

        ObjectNode mutableTemplate = (ObjectNode) this.getDatasetJsonTemplate();

        // Title
        ObjectNode objectNodeTitle = (ObjectNode) mutableTemplate.get("datasetVersion").get("metadataBlocks").get("citation").get("fields").get(0);
        objectNodeTitle.put("value", journal.getName());
        fields.put(objectNodeTitle);



        System.out.println(mutableTemplate.at("/datasetVersion/metadataBlocks/citation/fields").toString());



        Logger.getGlobal().warning("MUTABLE TEMPLATE: " + mutableTemplate);
        return null;
    }

    public JsonNode getDatasetJsonTemplate() {
        Stream<String> lines = null;
        try{
            Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("static/dataset-finch1.json")).toURI());
            lines = Files.lines(path);
            String data = lines.collect(Collectors.joining("\n"));

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(data);
        }
        catch(IOException | URISyntaxException e)
        {
            e.printStackTrace();
        } finally {
            assert lines != null;
            lines.close();
        }
        return null;
    }

    @Override
    public String exportJournal(Serializable id) throws IOException {
        return null;
    }

    @Override
    public String exportWorkspace(String APIKey) throws IOException {
        return null;
    }

    @Override
    public String exportWorkspace() throws IOException {
        return null;
    }

    public ObjectNode getJournal(String APIKey, int pageOffset)
    {
        return this.webClient
                .post()
                .uri(DataverseExportServiceImpl.baseURL)
                .header("X-Dataverse-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }
}
