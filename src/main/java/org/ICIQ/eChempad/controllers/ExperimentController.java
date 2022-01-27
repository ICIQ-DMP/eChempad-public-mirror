package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.exceptions.ExceptionResourceNotExists;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.UUID;

public interface ExperimentController {
    ResponseEntity<Set<Experiment>> getExperiments();

    ResponseEntity<Experiment> getExperiment(UUID uuid) throws ExceptionResourceNotExists;

    void addExperiment(Experiment experiment);

    void removeExperiment(UUID uuid) throws ExceptionResourceNotExists;

    void putExperiment(Experiment experiment, UUID uuid) throws ExceptionResourceNotExists;
}
