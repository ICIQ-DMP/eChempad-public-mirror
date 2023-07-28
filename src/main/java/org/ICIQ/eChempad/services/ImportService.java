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

import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

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
     * Reads the content that is in the root of the platform that we are reading from.
     *
     * @param APIKey API key used to authenticate in the platform that we are importing from.
     * @return A list of the elements that we read.
     */
    List<DataEntity> readRootEntities(String APIKey);

    /**
     * Expands the children of the received DataEntity by performing CRUD requests to read the children of this entity
     * in the origin platform. The children will be added to the DataEntity supplied by parameter. Only immediate
     * children will be expanded so further recursive uses will be needed to read the full hierarchy from the origin
     * platform.
     *
     * @param dataEntity The DataEntity that we are going to query in the origin platform for its children, which will
     *                   be added to this parameter to return the entities of the childre.
     * @param APIKey API key to authenticate in the platform that we are importing from.
     */
    void expandEntityChildren(DataEntity dataEntity, String APIKey);

    /**
     * Performs the update algorithm of the data supplied by parameter into the database of eChempad. This algorithm
     * has to
     *
     * @param dataEntity
     */
    void importEntity(DataEntity dataEntity);

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
