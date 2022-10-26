package org.ICIQ.eChempad.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.services.genericJPAServices.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    public String exportJournal(String APIKey, Serializable id) throws IOException {
        Optional<Journal> exportJournal = this.journalService.findById((UUID) id);

        if (! exportJournal.isPresent())
        {
            return "Could not export the journal " + id + ". It could not be found for the current user.";
        }

        this.webClient
                .post()
                .uri(DataverseExportServiceImpl.baseURL)
                .header("X-Dataverse-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();

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
        return this.webClient.get()
                .uri(SignalsImportServiceImpl.baseURL + "/entities?page[offset]=" + ((Integer) pageOffset).toString() + "&page[limit]=1&includeTypes=journal&include=owner&includeOptions=mine")
                .header("x-api-key", APIKey)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }
}
