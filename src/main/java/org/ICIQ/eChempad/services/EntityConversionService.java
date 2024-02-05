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

import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.DataEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;

/**
 * This interface exists to parse Journal, Experiment and Document into one another. This is clearly a design flaw.
 * This is a change that was proposed after I have already written the code for only having 3 nesting levels. Nesting
 * functionality should have been written using a composer pattern. I actually did not do it at first because my initial
 * specification was clearly intended for 3 nesting levels and no more, but after a year the specification changed, and
 * now I need to deal with a very suboptimal implementation.
 */
public interface EntityConversionService {
    /**
     * Transforms the received JPA entity into a Journal.
     *
     * @param entity A JPA entity to be parsed.
     * @return Journal with the metadata of the JPA entity in the parameter.
     */
    Container parseJournal(DataEntity entity);

    /**
     * Transforms the received JPA entity into a Document.
     *
     * @param entity A JPA entity to be parsed.
     * @return Document with the metadata of the JPA entity in the parameter.
     */
    Document parseDocument(DataEntity entity);
}
