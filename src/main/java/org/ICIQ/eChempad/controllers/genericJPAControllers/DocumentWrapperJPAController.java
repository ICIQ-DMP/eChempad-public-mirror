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
package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.DocumentWrapper;
import org.ICIQ.eChempad.configurations.wrappers.UploadFileResponse;
import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public interface DocumentWrapperJPAController<T extends EntityImpl, S extends Serializable> extends GenericJPAController<DocumentWrapper, UUID> {

    ResponseEntity<Resource> getDocumentData(@PathVariable(value = "id") UUID uuid, HttpServletRequest request) throws ResourceNotExistsException, NotEnoughAuthorityException;

    UploadFileResponse addDocumentToExperiment(@ModelAttribute("DocumentWrapper") DocumentWrapper documentWrapper, @PathVariable UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;

    Set<DocumentWrapper> getDocumentsFromExperiment(@PathVariable UUID experiment_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException;
}
