/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ResearcherServiceImpl extends GenericServiceImpl<Researcher, UUID> implements ResearcherService<Researcher, UUID> {

    @Autowired
    public ResearcherServiceImpl(ResearcherRepository researcherRepository) {
        super(researcherRepository);
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        // @TODO retrieve user from DB but retrieve authorities from the ACL
        return ((ResearcherRepository) (super.genericRepository)).loadUserByUsername(email);
    }

    @Override
    public Map<UUID, UserDetails> loadAllUserDetails() {
        return ((ResearcherRepository) (super.genericRepository)).loadAllUserDetails();
    }
}


