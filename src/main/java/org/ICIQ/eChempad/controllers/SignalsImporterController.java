package org.ICIQ.eChempad.controllers;

import org.springframework.http.ResponseEntity;

import java.io.Serializable;

/**
 * Models the contract that a class must fulfill to be an {@code SignalsImporterController}. It exposes basic methods to
 * import resources from Signals using its API into the workspace of the logged user.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 14/10/2022
 */
public interface SignalsImporterController extends ImporterController{

    /**
     * Imports all the workspace elements from the origin repository into the workspace of the currently logged user. It
     * uses the credentials saved in the {@code Researcher} instance associated with the logged user.
     *
     * @return Formatted response to include a summary of all the imported content.
     */
    ResponseEntity<String> importWorkspace();

    /**
     * Imports all the workspace elements from the origin repository into the workspace of the currently logged user. It
     * uses the credentials supplied to the method.
     *
     * @param APIKey Token to access the Signals API.
     * @return Formatted response to include a summary of all the imported content.
     */
    ResponseEntity<String> importWorkspace(String APIKey);

    /**
     * Import a journal identified by its EID from the origin repository into the workspace of the currently logged
     * user. It uses the credentials stored in the {@code Researcher} instance associated with the logged user.
     *
     * @param eid Identifier of the Journal to import in Signals application.
     * @return Formatted response to include a summary of all the imported content.
     */
    ResponseEntity<String> importJournal(Serializable eid);

    /**
     * Import a journal identified by its EID from the origin repository into the workspace of the currently logged
     * user using the provided API key.
     *
     * @param APIKey Token to access the Signals API.
     * @param eid Identifier of the journal to import.
     * @return Formatted response to include a summary of all the imported content.
     */
    ResponseEntity<String> importJournal(String APIKey, Serializable eid);
}
