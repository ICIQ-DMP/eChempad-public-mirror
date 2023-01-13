package org.ICIQ.eChempad.services;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.researchspace.dataverse.api.v1.DataverseAPI;
import com.researchspace.dataverse.api.v1.DataverseConfig;
import com.researchspace.dataverse.entities.DatasetFileList;
import com.researchspace.dataverse.entities.Identifier;
import com.researchspace.dataverse.http.DataverseAPIImpl;
import com.researchspace.dataverse.http.FileUploadMetadata;
import org.ICIQ.eChempad.configurations.converters.DocumentWrapperConverter;
import org.ICIQ.eChempad.configurations.wrappers.DataverseDatasetMetadata;
import org.ICIQ.eChempad.configurations.wrappers.DataverseDatasetMetadataImpl;
import org.ICIQ.eChempad.configurations.wrappers.UserDetailsImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Experiment;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;
import org.ICIQ.eChempad.services.genericJPAServices.DocumentService;
import org.ICIQ.eChempad.services.genericJPAServices.ExperimentService;
import org.ICIQ.eChempad.services.genericJPAServices.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

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

    /**
     * The base URL used in this class to attack an instance of Dataverse. This is hard-coded for the CSUC Dataverse.
     * Could be parametrized for other Dataverse instances.
     */
    private static final String baseURL = "https://dataverse.csuc.cat";

    /**
     * Encapsulates {@code Journal} manipulation business logic.
     */
    @Autowired
    JournalService<Journal, UUID> journalService;

    /**
     * Encapsulates {@code Experiment} manipulation business logic.
     */
    @Autowired
    ExperimentService<Experiment, UUID> experimentService;

    /**
     * Encapsulates {@code Document} manipulation business logic.
     */
    @Autowired
    DocumentService<Document, UUID> documentService;

    /**
     * Provides methods to transform {@code Document} to {@code DocumentWrapper} and vice versa.
     */
    @Autowired
    DocumentWrapperConverter documentWrapperConverter;

    /**
     * Class that performs HTTP requests simulating a Browser.
     */
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

        // Get researcher currently logged in to retrieve user data to fill metadata
        Researcher author = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getResearcher();

        // Initialize API client with the URL that we are going to attack and the API key to access it
        DataverseAPI api = new DataverseAPIImpl();
        URL url = null;
        try {
            url = new URL(DataverseExportServiceImpl.baseURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        DataverseConfig config = new DataverseConfig(url, APIKey, "ICIQ");
        api.configure(config);

        // Create and configure Dataverse API library
        // Basic metadata
        DataverseDatasetMetadata dataverseDatasetMetadata = new DataverseDatasetMetadataImpl();
        dataverseDatasetMetadata.setTitle(journalToExport.getName());
        dataverseDatasetMetadata.setAuthorAffiliation("ICIQ");
        dataverseDatasetMetadata.setAuthorName(author.getUsername());
        dataverseDatasetMetadata.setDatasetContactName(author.getUsername());
        dataverseDatasetMetadata.setDescription(journalToExport.getDescription());
        dataverseDatasetMetadata.setContactEmail(author.getUsername());
        // Subject metadata
        List<String> subjects = new ArrayList<>();
        subjects.add("Arts and Humanities");
        subjects.add("Medicine, Health and Life Sciences");
        dataverseDatasetMetadata.setSubjects(subjects);

        // Call Dataverse API client to create dataset into the ICIQ Dataverse
        Identifier datasetDatabaseIdentifier = api.getDataverseOperations().createDataset(dataverseDatasetMetadata.toString(), "ICIQ");

        // Upload files of each experiment into the created dataset
        // Get all experiments from selected journal
        for (Experiment experiment: this.experimentService.getExperimentsFromJournal((UUID) id))
        {
            // Get all documents for each experiment
            for (Document document: this.documentService.getDocumentsFromExperiment(((UUID) experiment.getId())))
            {
                try {
                    // Create File metadata for the upload
                    FileUploadMetadata fileUploadMetadata = FileUploadMetadata.builder()
                            .description(document.getDescription())
                            .directoryLabel(experiment.getName())
                            .build();

                    // Upload file
                    DatasetFileList datasetFileList = api.getDatasetOperations().uploadNativeFile(
                            document.getBlob().getBinaryStream(),
                            document.getFileSize(),
                            fileUploadMetadata,
                            datasetDatabaseIdentifier,
                            document.getName()
                    );

                } catch (SQLException e) {
                    // TODO throw exception
                    e.printStackTrace();
                }
            }
        }
        return datasetDatabaseIdentifier.toString();
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

    public ObjectNode getFileMetadata(String APIKey, String fileDOI) throws IOException
    {
        return this.webClient
                .get()
                .uri(DataverseExportServiceImpl.baseURL + "/api/files/:persistentId/metadata?persistentId=" + fileDOI)
                .header("X-Dataverse-key", APIKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
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
