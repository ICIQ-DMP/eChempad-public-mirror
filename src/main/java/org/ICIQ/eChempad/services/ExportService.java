package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;

import java.io.IOException;

/**
 * Defines the contract that a class that implements this interface has to fulfill in order to be an
 * {@code ExportService}.
 */
public interface ExportService {

    /**
     * By using the supplied API key, import all available material from
     * a third-party service, depending on the implementation of the class.
     * @throws IOException Thrown due to any type of connection problem.
     */
    String bulkExport(String APIKey) throws IOException;

    /**
     * By using the available API keys from the currently logged user if available, import all available material from
     * a third-party service, depending on the implementation of the class.
     * @throws IOException Thrown due to any type of connection problem.
     */
    String bulkExport() throws IOException;


}
