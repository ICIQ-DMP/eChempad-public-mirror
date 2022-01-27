package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.repositories.ExperimentRepository;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExperimentServiceClass extends GenericServiceClass<Experiment, UUID> implements ExperimentService {
    @Autowired
    public ExperimentServiceClass(ExperimentRepository experimentRepository) {
        super(experimentRepository);
        //       this.researcherRepository = (ResearcherRepository) genericRepository;
    }
}
