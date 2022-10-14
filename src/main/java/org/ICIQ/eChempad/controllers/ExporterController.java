package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;

import java.util.UUID;

/**
 * Models the contract that a class must fulfill to be a {@code ControllerExporter}. It exposes basic methods to export
 * resources from the workspace of the logged user into a running Dataverse instance using its API.
 *
 * @see <a href="https://guides.dataverse.org/en/latest/api/intro.html">...</a>
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
     * @param journal_uuid UUID of the journal that it has to export.
     */
    void exportJournal(UUID journal_uuid);

    /**
     * Exports all journals from the logged user workspace into another system. This method uses an API key stored in
     * the {@code UserDetails} of the currently logged user.
     */
    void exportWorkspace();
}
