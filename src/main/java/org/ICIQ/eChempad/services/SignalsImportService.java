/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ICIQ.eChempad.entities.DocumentWrapper;
import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.Date;


/**
 * Import service specialized in importing data from Signals application.
 */
public interface SignalsImportService extends ImportService {

    /**
     * Parses the JSON received from a Signals entity to obtain the date of that entity.
     *
     * @param metadataJSON JSON of the entity.
     * @return Date object
     */
    Date parseDateFromJSON(ObjectNode metadataJSON);

    /**
     * Parses the JSON received from a Signals entity to obtain the last update date of that entity.
     *
     * @param metadataJSON JSON of the entity.
     * @return Date object
     */
    Date parseUpdateDateFromJSON(ObjectNode metadataJSON);

    /**
     * Parses a date object from a String
     *
     * @param dateData String representing a date.
     * @return Parsed object.
     */
    Date parseDate(String dateData);

    /**
     * Gets a child from a Container that has its origin on Signals and returns it as a JSON representing its data.
     *
     * @param APIKey API key used for the retrieval of the child
     * @param pageOffset The number that identifies a certain child in the container
     * @param container_eid An ID that identifies the container in Signals
     * @return Returns an ObjectNode which is an in-memory representation of a JSON.
     */
    ObjectNode getChildFromContainer(String APIKey, int pageOffset, String container_eid);

    /**
     * Gets the JSON of a container and parses it to a Container object with the data of the JSON.
     *
     * @param containerJSON The in-memory JSON to parse.
     * @return A detached instance of a Container with the data of the JSON in it.
     */
    Container parseContainer(ObjectNode containerJSON);

    /**
     * Gets the JSON of a document and parses it to a Document object with the data of the JSON.
     *
     * @param documentJSON The in-memory JSON to parse.
     * @return A detached instance of a Document with the data of the JSON in it.
     */
    Document parseDocument(ObjectNode documentJSON);

    /**
     * Gets the JSON of a document and parses it to a DataEntity object with the data of the JSON. This method is used
     * for the common part of parseDocument and parseContainer.
     *
     * @param dataEntityJSON The in-memory JSON to parse,
     * @return An instance of a DataEntity with the data of the JSON in it.
     */
    DataEntity parseDataEntity(ObjectNode dataEntityJSON);

    /**
     * Parse file
     * First we obtain the inputStream of this document, which actually corresponds to a file. We also need
     * to obtain the values of the HTTP header "Content-Disposition" which looks like this in an arbitrary
     * document export:
     * <p>
     * attachment; filename="MZ7-085-DC_10%5B1%5D.zip"; filename*=utf-8''MZ7-085-DC_10%5B1%5D.zip
     * <p>
     * We would need to obtain the data of the filename which is the filed with the name "filename" and in
     * the previous example has the value "MZ7-085-DC_10%5B1%5D.zip"
     * We also need to obtain the header Content-type which indicates the mimetype of the file we are
     * retrieving. That will allow to know the type of file inorder to process it further.
     * File length is not needed to be parsed since it is conserved inside the byte-stream.
     *
     * @param documentWrapper The document to be set with the file.
     * @param APIKey The API key used to authenticate requests.
     */
    void expandDocumentFile(DocumentWrapper documentWrapper, String APIKey);

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







    // OLD METHODS
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