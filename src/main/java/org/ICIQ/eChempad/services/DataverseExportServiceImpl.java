package org.ICIQ.eChempad.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.researchspace.dataverse.api.v1.DataverseAPI;
import com.researchspace.dataverse.api.v1.DataverseConfig;
import com.researchspace.dataverse.entities.Dataset;
import com.researchspace.dataverse.entities.Identifier;
import com.researchspace.dataverse.http.DataverseAPIImpl;
import com.researchspace.dataverse.sword.FileUploader;
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
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.IRIElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.swordapp.client.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

import static com.researchspace.dataverse.entities.facade.PublicationIDType.doi;

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
        Logger.getGlobal().warning("A dataset with identifier " + identifier.getId() + "has been created into the ICIQ dataverse.");

        // Upload files of each experiment into the created dataset
        // Get all experiments from selected journal
        for (Experiment experiment: this.experimentService.getExperimentsFromJournal((UUID) id))
        {
            // Get all documents for each experiment
            for (Document document: this.documentService.getDocumentsFromExperiment(((UUID) experiment.getId())))
            {
                try {
                    /*
                     * Obtain DocumentWrapper from the currently selected Document in order to obtain a MultipartFile.
                     * This will be transformed into a File in order to fulfill the parameters of the uploadFile method.
                     * TODO When the PR of the repository is finished, we will be able to use a InputStream to upload a
                     *  File, so we do not have to transform it into DocumentWrapper or having to bulk the data into the
                     *  disk before uploading it.
                     */
                    DocumentWrapper documentWrapper = this.documentWrapperConverter.convertToEntityAttribute(document);
                    File file = DataverseExportServiceImpl.multipartToFile(documentWrapper.getFile(), documentWrapper.getId().toString());

                    // Obtain the dataset what we just created in order to obtain its DOI.
                    Dataset dataset = api.getDatasetOperations().getDataset(identifier);

                    // If we have a valid dataset, then we can
                    if (! dataset.getDoiId().isPresent())
                    {
                        // TODO throw exception
                        throw new RuntimeException();
                    }
                    else
                    {
                        // This line could do the job but the library is quite unuseful until my PR are merged back into the base repository.
                        // api.getDatasetOperations().uploadFile(dataset.getDoiId().get(), file);
                        // Instead, we unwrap the function and get the return info.
                        DepositReceipt depositReceipt = this.uploadFile(APIKey, dataset.getDoiId().get(), file);

                        // This code retrieves the identifier from SWORD library, which is actually a full URL to the
                        // uploaded file, which includes its DOI.
                        // With this code we trim the DOI from the link, which actually hurts me as software developer
                        // and user of this quite unuseful library.
                        String[] result = depositReceipt.getEntry().getId().toString().split("/");
                        String fileDOI = result[result.length - 2] + "/" + result[result.length - 1];

                        Logger.getGlobal().warning("iriDB is " + fileDOI);

                        // Obtain autogenerated file metadata in order to update it with metadata from the instances in
                        // echempad
                        ObjectNode objectNode = this.getFileMetadata(APIKey, fileDOI);
                        Logger.getGlobal().warning("metadata from object node is " + objectNode.toPrettyString());

                    }
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

    public DepositReceipt uploadFile(String APIKey, String dataset_DOI, File file)
    {
        FileUploader uploader = new FileUploader();
        try {
            return uploader.deposit(file, APIKey, new URI(DataverseExportServiceImpl.baseURL), dataset_DOI);
        } catch (IOException | SWORDClientException  | ProtocolViolationException | URISyntaxException e)
        {
            Logger.getGlobal().severe("Couldn't upload file " + file.getName() + " with doi " + doi.toString() + ": " + e.getMessage());
            throw new RestClientException(e.getMessage());
        } catch (SWORDError error)
        {
            Logger.getGlobal().severe("SwordError: " + error.getErrorBody());
            throw new RestClientException(error.getErrorBody());
        }
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
