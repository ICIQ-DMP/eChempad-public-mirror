package org.ICIQ.eChempad.services.importServices;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ICIQ.eChempad.entities.DocumentWrapper;
import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;

public interface SignalsAPIParsingService {

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
     * Parses a MultiPartFile from the headers of the response of the Signals API.
     *
     * @param exportedDocumentEID This will be used as the name field of the multipart file.
     * @param httpHeadersOfRequest From here we will extract the MediaType and the original file name.
     * @param inputStream This is the literal data inside the file.
     * @return MultipartFile instance with the data on it.
     */
    MultipartFile parseDocumentExport(String exportedDocumentEID, HttpHeaders httpHeadersOfRequest, InputStream inputStream);
}
