package org.ICIQ.eChempad.services;

import java.io.IOException;

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
    String bulkImport() throws IOException;
}
