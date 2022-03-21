package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.Experiment;
import org.ICIQ.eChempad.repositories.ExperimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class ExperimentServiceClass extends GenericServiceClass<Experiment, UUID> implements ExperimentService {

    @Autowired
    SecurityService securityService;

    @Autowired
    public ExperimentServiceClass(ExperimentRepository experimentRepository) {
        super(experimentRepository);
        //       this.researcherRepository = (ResearcherRepository) genericRepository;
    }

    @Override
    public Set<Experiment> getAll() {
        return this.securityService.getAuthorizedExperiment(Authority.READ);
    }



}
