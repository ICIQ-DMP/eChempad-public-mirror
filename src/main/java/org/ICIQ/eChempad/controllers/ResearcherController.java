package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Researcher;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.UUID;

/**
 * GET /researcher :
 *   Obtains all researchers.
 * GET /researcher/:id :
 *   Obtains a certain researcher instance via GET on the specific endpoint of that certain researcher, which is
 *   composed by its uuid.
 * POST /researcher :
 *   Adds a new researcher instance by submitting the required fields of the researcher.
 * PUT /researcher/:id :
 *   Modifies the data of a researcher by supplying the fields that will be modified. Return error if UUID not found.
 * DELETE /researcher/:id
 *   Deletes a researcher from the database from its id. Return error if UUID not found.
 */
public interface ResearcherController {
    Set<Researcher> getAllResearchers();

    ResponseEntity<Researcher> getResearcher(UUID uuid);

    Researcher addResearcher(Researcher researcher);

    int removeResearcher(UUID uuid);

    Researcher putResearcher(Researcher researcher, UUID uuid);
}