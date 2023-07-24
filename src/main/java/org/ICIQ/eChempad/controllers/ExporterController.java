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
package org.ICIQ.eChempad.controllers;

import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 * Models the contract that a class must fulfill to be a {@code ControllerExporter}. It exposes basic methods to export
 * resources from the workspace of the logged user into a running Dataverse instance using its API.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 14/10/2022
 */
public interface ExporterController {

    /**
     * Exports a journal from the logged user workspace identified by its UUID into another system. This method uses an
     * API key stored in the {@code UserDetails} of the currently logged user.
     *
     * @see org.springframework.security.core.context.SecurityContextHolder
     * @see org.ICIQ.eChempad.configurations.wrappers.UserDetailsImpl
     * @see org.ICIQ.eChempad.entities.genericJPAEntities.Researcher
     * @param journal_id UUID of the journal that it has to export.
     * @return String containing a summary of the imported entities, wrapped around a {@code ResponseEntity}.
     */
    ResponseEntity<String> exportJournal(UUID journal_id);

    /**
     * Exports all journals from the logged user workspace into another system. This method uses an API key stored in
     * the {@code UserDetails} of the currently logged user.
     *
     * @return String containing a summary of the imported entities, wrapped around a {@code ResponseEntity}.
     */
    ResponseEntity<String> exportWorkspace();

    /**
     * Exports a journal from the logged user workspace identified by its UUID into another system. This method uses an
     * API key supplied with the request.
     *
     * @param journal_id Identifier of the journal to export.
     * @param APIKey APIkey used to log into the service that will receive the data in order to use its API.
     * @return String containing a summary of the exported entities, wrapped around a {@code ResponseEntity}
     */
    ResponseEntity<String> exportJournalWithKey(UUID journal_id, String APIKey);

    /**
     * Exports all journals from the logged user workspace into another system. This method uses an API key supplied
     * with the request.
     *
     * @param APIKey APIkey used to log into the service that will receive the data in order to use its API.
     * @return String containing a summary of the exported entities, wrapped around a {@code ResponseEntity}
     */
    ResponseEntity<String> exportWorkspace(String APIKey);
}
