package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ResearcherServiceClass {

    @Autowired
    private ResearcherRepository researcherRepository;

}
