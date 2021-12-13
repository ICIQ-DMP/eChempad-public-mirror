package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.services.ResearcherService;
import org.ICIQ.eChempad.services.ResearcherServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.HashPrintJobAttributeSet;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;


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
@RestController
@RequestMapping("/api/researcher")
public class ResearcherControllerClass implements ResearcherController {

    @Autowired
    private ResearcherServiceClass researcherServiceClass;

    public ResearcherControllerClass(ResearcherServiceClass researcherServiceClass) {
        this.researcherServiceClass = researcherServiceClass;
    }

    @Override
    @GetMapping(
            value = "",
            produces = "application/json"
    )
    public Set<Researcher> getAllResearchers() {
        return new HashSet<>(this.researcherServiceClass.getAll());
    }

    // https://stackoverflow.com/questions/30967822/when-do-i-use-path-params-vs-query-params-in-a-restful-api
    // https://restfulapi.net/http-methods/
    @Override
    @GetMapping(
            value = "/{id}",
            produces = "application/json"
    )
    public ResponseEntity<Researcher> getResearcher(@PathVariable(value = "id") UUID uuid) {
        Researcher researcher = this.researcherServiceClass.get(uuid);

        if (researcher != null)
        {
            return ResponseEntity.ok().body(researcher);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(
            value = "",
            produces = "application/json",
            consumes = "application/json"
    )
    public Researcher addResearcher(@Validated @RequestBody Researcher researcher) {
        return this.researcherServiceClass.saveOrUpdate(researcher);
    }



    @DeleteMapping(
            value = "/{id}",
            produces = "application/json"
    )
    public int removeResearcher(@PathVariable(value = "id") UUID uuid) {

        return this.researcherServiceClass.remove(uuid);
    }

    @PutMapping(
            value = "/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    @Override
    public Researcher putResearcher(@Validated @RequestBody Researcher researcher, @PathVariable(value = "id") UUID uuid) {
        return this.researcherServiceClass.update(researcher, uuid);
    }

}
