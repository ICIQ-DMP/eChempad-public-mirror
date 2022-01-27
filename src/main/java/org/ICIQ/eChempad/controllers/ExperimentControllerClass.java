package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.exceptions.ExceptionResourceNotExists;
import org.ICIQ.eChempad.services.ExperimentServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/experiment")
public class ExperimentControllerClass implements ExperimentController{
    private ExperimentServiceClass experimentServiceClass;

    @Autowired
    public ExperimentControllerClass(ExperimentServiceClass experimentServiceClass) {
        this.experimentServiceClass = experimentServiceClass;
    }

    @Override
    @GetMapping(
            value = "",
            produces = "application/json"
    )
    public ResponseEntity<Set<Experiment>> getExperiments() {
        HashSet<Experiment> experiments = new HashSet<>(this.experimentServiceClass.getAll());
        return ResponseEntity.ok(experiments);
    }

    // https://stackoverflow.com/questions/30967822/when-do-i-use-path-params-vs-query-params-in-a-restful-api
    // https://restfulapi.net/http-methods/
    @Override
    @GetMapping(
            value = "/{id}",
            produces = "application/json"
    )
    public ResponseEntity<Experiment> getExperiment(@PathVariable(value = "id") UUID uuid) throws ExceptionResourceNotExists {
        Experiment experiment = this.experimentServiceClass.get(uuid);
        return ResponseEntity.ok().body(experiment);
    }


    @PostMapping(
            value = "",
            consumes = "application/json"
    )
    public void addExperiment(@Validated @RequestBody Experiment experiment) {
        this.experimentServiceClass.saveOrUpdate(experiment);
    }


    @DeleteMapping(
            value = "/{id}",
            produces = "application/json"
    )
    public void removeExperiment(@PathVariable(value = "id") UUID uuid) throws ExceptionResourceNotExists {
        this.experimentServiceClass.remove(uuid);
    }

    @PutMapping(
            value = "/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    @Override
    public void putExperiment(@Validated @RequestBody Experiment experiment, @PathVariable(value = "id") UUID uuid) throws ExceptionResourceNotExists {
        this.experimentServiceClass.update(experiment, uuid);
    }

}
