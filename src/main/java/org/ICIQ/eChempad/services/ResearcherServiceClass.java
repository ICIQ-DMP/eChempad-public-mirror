package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.GenericRepository;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ResearcherServiceClass extends GenericServiceClass<Researcher, UUID> implements ResearcherService{

    @Autowired
    private ResearcherRepository researcherRepository;

    public ResearcherServiceClass(GenericRepository<Researcher, UUID> genericRepository) {
        super(genericRepository);
        this.researcherRepository = (ResearcherRepository) genericRepository;
    }
}
