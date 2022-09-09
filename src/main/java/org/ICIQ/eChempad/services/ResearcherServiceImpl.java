/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.repositories.AclRepository;
import org.ICIQ.eChempad.repositories.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

@Service
public class ResearcherServiceImpl<T extends IEntity, S extends Serializable>  extends GenericServiceImpl<Researcher, UUID> implements ResearcherService<Researcher, UUID> {

    @Autowired
    public ResearcherServiceImpl(ResearcherRepository<T, S> researcherRepository, AclRepository aclRepository) {
        super(researcherRepository, aclRepository);
    }

    @Override
    public Researcher loadUserByUsername(String email) {
        // @TODO retrieve user from DB but retrieve authorities from the ACL
        return ((ResearcherRepository<Researcher, UUID>) (super.genericRepository)).findByUsername(email);
    }

}


