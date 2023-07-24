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
