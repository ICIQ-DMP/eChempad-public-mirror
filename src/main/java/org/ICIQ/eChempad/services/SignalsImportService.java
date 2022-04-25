package org.ICIQ.eChempad.services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;

public interface SignalsImportService {

    /**
     * Imports all the data accessible from signals relative to the current user.
     * @throws IOException Thrown if something is wrong during all the communication with Signals API
     */
    void importSignals(String APIKey) throws IOException;

    /**
     * Scraps all the journal accessible with the supplied API key until the retrieved data field is an empty JSON
     * array.
     * @param APIKey APIKey used for the authorization against the Signals web server application.
     * @return Array of JSON nodes. Each position of the array corresponds to a single journal.
     */
    ArrayNode getJournals(String APIKey);


    /**
     * Obtains the ByteArray corresponding to a file that has been downloaded from the Signals API, identified by the
     * supplied UUID.
     *
     * https://iciq.signalsnotebook.perkinelmercloud.eu/docs/extapi/swagger/index.html#/Entities/downloadEntityContent
     *
     * Obtains a document (entity) identified by the supplied UUID. The value of the "format" parameter and "Accept"
     * header can be configured depending on the format that we want to export the entity to and the type of entity we
     * are exporting. This implementation depends on the implementation of the Signals Notebook.
     *
     * For entities of type "chemical structures", the "format" parameter can be:
     *  - cdxml, will export chemical structure as CDXML file.
     *  - svg, will export chemical structure as SVG file.
     *  - mol, will export chemical structure as V2000 MOL file.
     *  - mol-v3000, will export chemical structure as V3000 MOL file.
     *  - smiles, will export chemical structure as SMILES file.
     *  - (blank), will export chemical structure as CDXML file.
     *
     * When treating an entity of type "chemical structures" we can specify the contents of the "Accept" header too:
     * image/svg+xml, will export chemical structure as SVG file.
     * chemical/x-cdxml, will export chemical structure as CDXML file.
     * chemical/x-mdl-molfile, will export chemical structure as V2000 molfile.
     * chemical/mdl-molfile, will export chemical structure as V2000 molfile.
     * chemical/x-mdl-molfile-v3000, will export chemical structure as V3000 molfile.
     * chemical/mdl-molfile-v3000, will export chemical structure as V3000 molfile.
     * chemical/x-daylight-smiles, will export chemical structure as SMILES file.
     * * / *, get format from parameter 'format' then export.
     * application/octet-stream, get format from parameter 'format' then export.
     * (blank), get format from parameter 'format' then export.
     *
     *
     * For entities of type "samplesContainer" / "paragrid" / "materialsTable", the "format" parameter can be:
     *  - csv, will export to CSV file.
     *  - sdf, will export to SDF file.
     *  - (blank), will export to CSV file.
     *
     * When treating an entity of type "samplesContainer" / "paragrid" / "materialsTable" we can specify the contents of
     * the "Accept" header too:
     * text/csv, will export to CSV file.
     * chemical/x-mdl-sdfile, will export to SDF file.
     * (blank), will export to CSV file(DO NOT set 'format').
     *
     * @param APIKey API Key used to authorize the access to the API methods of the Signals UI
     * @param document_eid EID of the document that we are retrieving from Signals API
     * @return Data that represents a generic file.
     */
    ByteArrayResource exportDocument(String APIKey, String document_eid);

}
