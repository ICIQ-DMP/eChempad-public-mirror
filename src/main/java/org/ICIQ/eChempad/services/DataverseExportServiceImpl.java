package org.ICIQ.eChempad.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.researchspace.dataverse.api.v1.DataverseAPI;
import com.researchspace.dataverse.api.v1.DataverseConfig;
import com.researchspace.dataverse.entities.DataverseGet;
import com.researchspace.dataverse.entities.Identifier;
import com.researchspace.dataverse.entities.facade.DatasetFacade;
import com.researchspace.dataverse.http.DataverseAPIImpl;
import org.ICIQ.eChempad.configurations.converters.DocumentWrapperConverter;
import org.ICIQ.eChempad.configurations.wrappers.DataverseDatasetMetadata;
import org.ICIQ.eChempad.configurations.wrappers.DataverseDatasetMetadataImpl;
import org.ICIQ.eChempad.configurations.wrappers.UserDetailsImpl;
import org.ICIQ.eChempad.entities.DocumentWrapper;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Experiment;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;
import org.ICIQ.eChempad.services.genericJPAServices.DocumentService;
import org.ICIQ.eChempad.services.genericJPAServices.ExperimentService;
import org.ICIQ.eChempad.services.genericJPAServices.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.print.Doc;
import java.io.File;
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
    ExperimentService<Experiment, UUID> experimentService;

    @Autowired
    DocumentService<Document, UUID> documentService;

    @Autowired
    DocumentWrapperConverter documentWrapperConverter;

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
        // Search journal to export in the current database
        Optional<Journal> exportJournal = this.journalService.findById((UUID) id);
        if (! exportJournal.isPresent())
        {
            // TODO throw exception
            return "Could not export the journal " + id + ". It could not be found for the current user.";
        }
        Journal journalToExport = exportJournal.get();

        // Get researcher currently logged in to retrieve user data.
        Researcher author = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getResearcher();

        // Create and configure dataverse API library
        DataverseDatasetMetadata dataverseDatasetMetadata = new DataverseDatasetMetadataImpl();
        dataverseDatasetMetadata.setTitle(journalToExport.getName());
        dataverseDatasetMetadata.setAuthorAffiliation("ICIQ");
        dataverseDatasetMetadata.setAuthorName(author.getUsername());
        dataverseDatasetMetadata.setDatasetContactName(author.getUsername());
        dataverseDatasetMetadata.setDescription(journalToExport.getDescription());
        dataverseDatasetMetadata.setContactEmail(author.getUsername());
        List<String> subjects = new ArrayList<>();
        subjects.add("Arts and Humanities");
        subjects.add("Medicine, Health and Life Sciences");
        dataverseDatasetMetadata.setSubjects(subjects);

        // Initialize API client
        DataverseAPI api = new DataverseAPIImpl();
        URL url = null;
        try {
            url = new URL(DataverseExportServiceImpl.baseURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        DataverseConfig config = new DataverseConfig(url, APIKey, "ICIQ");
        api.configure(config);

        // Call Dataverse API client to create dataset into the ICIQ dataverse
        Identifier identifier = api.getDataverseOperations().createDataset(dataverseDatasetMetadata.toString(), "ICIQ");
        Logger.getGlobal().warning("THE IDENTIFIER " + identifier);
        DataverseGet dataverseGet = api.getDataverseOperations().getDataverseById(identifier.toString());
        Logger.getGlobal().warning("THE DATASET GET " + dataverseGet);
        String idCreatedDataset = dataverseGet.getId();
        Logger.getGlobal().warning("THE DATASET id " + idCreatedDataset);

        // Upload files of each experiment into the created dataset
        // Get all experiments from selected journal
        for (Experiment experiment: this.experimentService.getExperimentsFromJournal((UUID) id))
        {
            // Get all documents for each experiments
            for (Document document: this.documentService.getDocumentsFromExperiment(((UUID) experiment.getId())))
            {
                // Obtain DocumentWrapper from the current selected Document
                DocumentWrapper documentWrapper = this.documentWrapperConverter.convertToEntityAttribute(document);

                Logger.getGlobal().warning(documentWrapper.getId().toString());
                try {
                    File file = DataverseExportServiceImpl.multipartToFile(documentWrapper.getFile(), documentWrapper.getId().toString());
                    api.getDatasetOperations().uploadFile(idCreatedDataset, file);
                } catch (IOException e) {
                    // TODO throw exception
                    e.printStackTrace();
                }
            }
        }

        return identifier.toString();
    }

    public static File multipartToFile(MultipartFile multipart, String fileNameWithoutCollisions) throws IllegalStateException, IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileNameWithoutCollisions);
        multipart.transferTo(convFile);
        return convFile;
    }

    @Override
    public String exportJournal(Serializable id) throws IOException {
        return exportJournal( ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getResearcher().getDataverseAPIKey(), id);
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
