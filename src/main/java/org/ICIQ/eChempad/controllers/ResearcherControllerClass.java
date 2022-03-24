package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.exceptions.ExceptionResourceNotExists;
import org.ICIQ.eChempad.services.ResearcherServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
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

/**
 * The controller will be responsible of processing REST API requests and transforming them into "model" requests,
 * which are programmatic requests for the service layer.
 * Then, queries will be made to the service layer.
 * After that, the data received from the service layer will be transformed in a ResponseEntity to render the Views of
 * the app.
 */
@RestController
@RequestMapping("/api/researcher")
public class ResearcherControllerClass implements ResearcherController {

    // https://blog.marcnuri.com/inyeccion-de-campos-desaconsejada-field-injection-not-recommended-spring-ioc
    private final ResearcherServiceClass researcherServiceClass;

    @Autowired
    public ResearcherControllerClass(ResearcherServiceClass researcherServiceClass) {
        this.researcherServiceClass = researcherServiceClass;
    }

    @Override
    @GetMapping(
            value = "",
            produces = "application/json"
    )
    public ResponseEntity<Set<Researcher>> getResearchers() {
        HashSet<Researcher> researchers = new HashSet<>(this.researcherServiceClass.getAll());
        return ResponseEntity.ok(researchers);
    }

    // https://stackoverflow.com/questions/30967822/when-do-i-use-path-params-vs-query-params-in-a-restful-api
    // https://restfulapi.net/http-methods/
    @Override
    @GetMapping(
            value = "/{id}",
            produces = "application/json"
    )
    public ResponseEntity<Researcher> getResearcher(@PathVariable(value = "id") UUID uuid) throws ExceptionResourceNotExists {
        Researcher researcher = this.researcherServiceClass.get(uuid);
        return ResponseEntity.ok().body(researcher);
    }


    @PostMapping(
            value = "",
            consumes = "application/json"
    )
    public void addResearcher(@Validated @RequestBody Researcher researcher) {
        this.researcherServiceClass.save(researcher);
    }


    @DeleteMapping(
            value = "/{id}",
            produces = "application/json"
    )
    public void removeResearcher(@PathVariable(value = "id") UUID uuid) throws ExceptionResourceNotExists {
        this.researcherServiceClass.remove(uuid);
    }

    @PutMapping(
            value = "/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    @Override
    public void putResearcher(@Validated @RequestBody Researcher researcher, @PathVariable(value = "id") UUID uuid) throws ExceptionResourceNotExists {
        this.researcherServiceClass.update(researcher, uuid);
    }

}
