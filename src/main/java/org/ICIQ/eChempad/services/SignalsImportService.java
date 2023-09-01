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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.DoubleConsumer;


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
     * Parses a date object from an String
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
     *
     * attachment; filename="MZ7-085-DC_10%5B1%5D.zip"; filename*=utf-8''MZ7-085-DC_10%5B1%5D.zip
     *
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

}