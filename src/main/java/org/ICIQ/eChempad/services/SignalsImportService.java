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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ICIQ.eChempad.entities.genericJPAEntities.Container;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Import service specialized in importing data from Signals application.
 */
public interface SignalsImportService extends ImportService {

    /**
     * Parses the JSON received from a Signals entity to obtain the date of that entity.
     *
     * @param metadataJSON JSON of the entity.
     * @return Date object
     */
    Date parseDateFromJSON(ObjectNode metadataJSON);

    /**
     * Parses the JSON received from a Signals entity to obtain the last update date of that entity.
     *
     * @param metadataJSON JSON of the entity.
     * @return Date object
     */
    Date parseUpdateDateFromJSON(ObjectNode metadataJSON);

    /**
     * Parses a date object from an String
     *
     * @param dateData String representing a date.
     * @return Parsed object.
     */
    Date parseDate(String dateData);

    /**
     * Gets a child from a Container that has its origin on Signals and returns it as a JSON representing its data.
     *
     * @param APIKey API key used for the retrieval of the child
     * @param pageOffset The number that identifies a certain child in the container
     * @param container_eid An ID that identifies the container in Signals
     * @return Returns an ObjectNode which is an in-memory representation of a JSON.
     */
    ObjectNode getChildFromContainer(String APIKey, int pageOffset, String container_eid);

    /**
     * Gets the JSON of a container and parses it to a Container object with the data of the JSON.
     *
     * @param containerJSON The in-memory JSON to parse.
     * @return A detached instance of a Container with the data of the JSON in it.
     */
    Container parseContainer(ObjectNode containerJSON);
}