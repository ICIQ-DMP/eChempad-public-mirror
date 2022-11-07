package org.ICIQ.eChempad.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.researchspace.dataverse.api.v1.DataverseAPI;
import com.researchspace.dataverse.api.v1.DataverseConfig;
import com.researchspace.dataverse.http.DataverseAPIImpl;
import org.ICIQ.eChempad.configurations.wrappers.DataverseDatasetMetadata;
import org.ICIQ.eChempad.configurations.wrappers.DataverseDatasetMetadataImpl;
import org.ICIQ.eChempad.configurations.wrappers.UserDetailsImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;
import org.ICIQ.eChempad.services.genericJPAServices.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

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

    private static final String baseURL = "https://dataverse.csuc.cat";

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

        Researcher author = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getResearcher();

        DataverseDatasetMetadata dataverseDatasetMetadata = new DataverseDatasetMetadataImpl();

        dataverseDatasetMetadata.setTitle(journal.getName());
        dataverseDatasetMetadata.setAuthorAffiliation("ICIQ");
        dataverseDatasetMetadata.setAuthorName(author.getUsername());
        dataverseDatasetMetadata.setDatasetContactName(author.getUsername());
        dataverseDatasetMetadata.setDescription(journal.getDescription());
        dataverseDatasetMetadata.setContactEmail(author.getUsername());

        List<String> subjects = new ArrayList<>();
        subjects.add("Medicine");
        subjects.add("computing");
        dataverseDatasetMetadata.setSubjects(subjects);

        DataverseAPI api = new DataverseAPIImpl();
        URL url = null;
        try {
            url = new URL(DataverseExportServiceImpl.baseURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        DataverseConfig config = new DataverseConfig(url, APIKey, "ICIQ");
        api.configure(config);
        // now you can call
        api.getDataverseOperations().createDataset(dataverseDatasetMetadata.toString(), "ICIQ");

        ObjectNode objectNode = this.webClient
                .post()
                .uri(DataverseExportServiceImpl.baseURL + "/api/dataverses/ICIQ/datasets")
                .body(BodyInserters.fromValue(dataverseDatasetMetadata))
                .headers(httpHeaders -> {
                    httpHeaders.add("X-Dataverse-key", APIKey);
                    httpHeaders.add("Content-Type", "application/json");
                })
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();

        Logger.getGlobal().warning("MUTABLE TEMPLATE after: " + dataverseDatasetMetadata);
        assert objectNode != null;
        return objectNode.toString();
    }


    @Override
    public String exportJournal(Serializable id) throws IOException {

        // ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getResearcher().getDataverseAPIKey();
        exportJournal("" , id);
        return "yes";
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
