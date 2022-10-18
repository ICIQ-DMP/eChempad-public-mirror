package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;

import java.io.IOException;

/**
 * Defines a basic contract for all the classes that import data from a third service, such as Signals.
 */
public interface ImportService {


    /**
     * By using the supplied API key, import all available material from
     * a third-party service, depending on the implementation of the class.
     * @throws IOException Thrown due to any type of connection problem.
     */
    String bulkImport(String APIKey) throws IOException;

    /**
     * By using the available API keys from the currently logged user if available, import all available material from
     * a third-party service, depending on the implementation of the class.
     * @throws IOException Thrown due to any type of connection problem.
     */
    String bulkImport() throws IOException;

    /**
     * By using the available API keys from the currently logged user if available, import all available material from
     * a third-party service, depending on the implementation of the class.
     * @param journal
     */
    void exportJournal(Journal journal);


}
