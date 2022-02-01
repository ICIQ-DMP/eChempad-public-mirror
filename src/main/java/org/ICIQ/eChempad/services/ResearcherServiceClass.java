package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.GenericRepository;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class ResearcherServiceClass extends GenericServiceClass<Researcher, UUID> implements ResearcherService{

    //private ResearcherRepository researcherRepository;

    @Autowired
    public ResearcherServiceClass(ResearcherRepository researcherRepository) {
        super(researcherRepository);
 //       this.researcherRepository = (ResearcherRepository) genericRepository;
    }


    @Override
    public Researcher saveOrUpdate(Researcher researcher) {

        return this.genericRepository.saveOrUpdate(researcher);
    }
}
