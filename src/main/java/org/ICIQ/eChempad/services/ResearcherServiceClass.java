package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.GenericRepository;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.ICIQ.eChempad.repositories.ResearcherRepositoryClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class ResearcherServiceClass extends GenericServiceImplementation<Researcher, UUID> implements ResearcherService{

    @Autowired
    private ResearcherRepository researcherRepository;

    public ResearcherServiceClass(GenericRepository<Researcher, UUID> genericRepository) {
        super(genericRepository);
        this.researcherRepository = (ResearcherRepository) genericRepository;
    }
}
