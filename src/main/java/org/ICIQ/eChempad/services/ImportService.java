package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;

import java.io.IOException;
import java.io.Serializable;

/**
 * Defines a basic contract for all the classes that import data from a third service, such as Signals.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 24/10/2022
 **/
public interface ImportService {


    /**
     * By using the supplied API key, import all available material from a third-party service, depending on the
     * implementation of the class.
     *
     * @param APIKey Token to log into the associated third-party application.
     * @return String containing a summary of the imported elements.
     * @throws IOException Thrown due to any type of connection problem.
     */
    String importWorkspace(String APIKey) throws IOException;

    /**
     * By using the available API keys from the currently logged user if available, import all available material from
     * a third-party service, depending on the implementation of the class.
     *
     * @return String containing a summary of the imported elements.
     * @throws IOException Thrown due to any type of connection problem.
     */
    String importWorkspace() throws IOException;

    /**
     * By using the supplied API key, import the selected journal from a third-party service, depending on the
     * implementation of the class.
     *
     * @param APIKey Contains a token to log into the associated third-party application.
     * @param id Identifies the journal to import from the third-party service.
     * @return String containing a summary of the imported elements.
     */
    String importJournal(String APIKey, Serializable id);

    /**
     * By using the available API keys from the currently logged user if available, import all available material from
     * a third-party service, depending on the implementation of the class.
     *
     * @param id Contains an identifier in the "other" repository for the journal that we want to import.
     * @return String containing a summary of the imported elements.
     */
    String importJournal(Serializable id);
}
