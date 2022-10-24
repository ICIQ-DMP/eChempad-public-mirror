package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;

import java.io.IOException;
import java.io.Serializable;

/**
 * Defines the contract that a class that implements this interface has to fulfill in order to be an
 * {@code ExportService}. A class inheriting this interface must implement these two methods, which provide a basic way
 * to retrieve data elements from a third-party service.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 24/10/2022
 **/
public interface ExportService {

    /**
     * By using the supplied API key, import all available material from
     * a third-party service, depending on the implementation of the class.
     * @throws IOException Thrown due to any type of connection problem.
     */
    String exportWorkspace(String APIKey) throws IOException;

    /**
     * By using the available API keys from the currently logged user if available, import all available material from
     * a third-party service, depending on the implementation of the class.
     * @throws IOException Thrown due to any type of connection problem.
     */
    String exportWorkspace() throws IOException;

    /**
     * By using the supplied API key, export the selected journal to a third-party service, depending on the
     * implementation of the class.
     *
     * @param APIKey Contains a token to log into the associated third-party application.
     * @param id Identifies the journal to export to the third-party service.
     * @throws IOException Throws exception if there is any IO error due to connection failure.
     * @return String containing a summary of the imported elements.
     */
    String exportJournal(String APIKey, Serializable id) throws IOException;

    /**
     * By using the available API keys from the currently logged user if available, export the selected journal to
     * a third-party service, depending on the implementation of the class.
     *
     * @param id Contains an identifier for the journal that we want to export.
     * @throws IOException Throws exception if there is any IO error due to connection failure.
     * @return String containing a summary of the imported elements.
     */
    String exportJournal(Serializable id) throws IOException;


}
