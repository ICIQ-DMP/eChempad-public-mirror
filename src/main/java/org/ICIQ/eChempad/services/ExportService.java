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
