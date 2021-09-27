package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class ResearcherServiceClass extends GenericServiceImplementation<Researcher, UUID> implements ResearcherService{

    @Autowired
    private ResearcherRepository researcherRepository;



}
