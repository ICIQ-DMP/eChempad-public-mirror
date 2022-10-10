package org.ICIQ.eChempad.services;

import java.io.IOException;

/**
 * Defines a basic contract for all the classes that import data from a third service, such as Signals.
 */
public interface ImportService {

    /**
     * By using the available API keys from the currently logged user if available, import all available material from
     * a third-party service, depending on the implementation of the class.
     * @throws IOException Thrown due to any type of connection problem.
     */
    void bulkImport() throws IOException;

}
