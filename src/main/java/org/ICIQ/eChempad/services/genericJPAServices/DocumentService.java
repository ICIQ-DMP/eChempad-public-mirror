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
package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public interface DocumentService<T extends EntityImpl, S extends Serializable> extends GenericService<Document, UUID> {

    /**
     * Adds a document to a certain experiment using the data in the document helper class and returns the new Document
     * instance.
     * @param document Data of a detached document instance inside a helper class that should have all the equivalent
     *                 fields.
     * @param container_uuid UUID of the experiment that we want to edit by adding this document.
     * @return Managed instance of the created document.
     */
    Document addDocumentToContainer(Document document, UUID container_uuid);

    /**
     * Get all documents that belong to a certain container designated by its UUID.
     * @param container_uuid UUID of the container we are retrieving.
     * @return Set of documents of this container. It could be empty.
     */
    Set<Document> getDocumentsFromContainer(UUID container_uuid);

}
