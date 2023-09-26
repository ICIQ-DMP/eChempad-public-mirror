package org.ICIQ.eChempad.services.importServices;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

public interface SignalsAPIService {

    /**
     * Performs query to retrieve the file that is stored inside a Document as a ByteArrayResource. It also reads all
     * the headers received in the query and returns them with th receivedHeaders parameter.
     *
     * @param APIKey API key to authenticate requests.
     * @param document_eid Unique identifier of the document in the Signals Service
     * @param receivedHeaders Output parameter. It is used by the caller to store the different headers received in the
     *                        query
     * @return The read file as a ByteArrayResource.
     * @throws IOException Thrown if something goes wrong during the connection.
     */
    ByteArrayResource exportDocumentFile(String APIKey, String document_eid, HttpHeaders receivedHeaders) throws IOException;

    /**
     * Reads a generic entity from Signals using its unique identifier in Signals
     *
     * @param APIKey API key to authenticate requests.
     * @param journalEUID Identifier of the journal in Signals.
     * @return Representation of the entity using JSON representation as ObjectNode.
     */
    ObjectNode getEntityWithEUID(String APIKey, String journalEUID);


    /**
     * Reads a single journal (root container) from Signals that is identified with a certain offset.
     *
     * @param APIKey APIKey used to authenticate requests to Signals
     * @param pageOffset The number of the journal that we need to read.
     * @return Journal in raw JSON representation.
     */
    ObjectNode getJournalWithOffset(String APIKey, int pageOffset);

    /**
     * Gets a child from a Container that has its origin on Signals and returns it as a JSON representing its data.
     *
     * @param APIKey API key used for the retrieval of the child
     * @param pageOffset The number that identifies a certain child in the container
     * @param container_eid An ID that identifies the container in Signals
     * @return Returns an ObjectNode which is an in-memory representation of a JSON.
     */
    ObjectNode getChildFromContainer(String APIKey, int pageOffset, String container_eid);

    // OLD UNUSED METHODS
    /**
     * Reads a single document from Signals that is identified with a certain offset and is children of the experiment
     * identified by the supplied eid.
     *
     * @param APIKey API key used to authenticate requests.
     * @param pageOffset The number of documents that we need to skip before reading the document that we need.
     * @param experiment_eid The unique identifier of that experiment in journal.
     * @return JSON representation of the Document metadata.
     */
    ObjectNode getDocumentFromExperiment(String APIKey, int pageOffset, String experiment_eid);

    ObjectNode getUserData(String APIKey, int userNumber);
}
