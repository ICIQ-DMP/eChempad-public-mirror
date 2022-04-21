package org.ICIQ.eChempad.services;

import java.io.IOException;

public interface SignalsImportService {

    /**
     * Imports all the data accessible from signals relative to the current user.
     * @throws IOException Thrown if something is wrong during all the communication with Signals API
     */
    void importSignals() throws IOException;

}
