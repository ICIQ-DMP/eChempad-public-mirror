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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ResearcherServiceClass extends GenericServiceClass<Researcher, UUID> implements ResearcherService {

    @Autowired
    public ResearcherServiceClass(ResearcherRepository researcherRepository) {
        super(researcherRepository);
    }


    @Override
    public Researcher save(Researcher researcher) {

        return this.genericRepository.saveOrUpdate(researcher);
    }

    @Override
    public Map<UUID, UserDetails> loadAllUserDetails() {
        Map<UUID, UserDetails> ret = new HashMap<>();

        for (Researcher res: this.genericRepository.getAll())
        {
            ret.put(res.getUUid(), this.loadUserByUsername(res.getEmail()));
        }
        return ret;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        /**Optional<Researcher> selected = ((ResearcherRepository) this.genericRepository).getResearcherByEmail(s);

        if (selected.isPresent())
        {
            // Obtain researcher by email (username)
            Researcher researcher = selected.get();

            // Obtain the roles of this user to construct the instance of UserDetails for SpringBoot Security.
            // TODO: to simplify only using a single Role per user, change to a Set in production if needed
            Set<Role> roles = researcher.getRoles();

            return org.springframework.security.core.userdetails.User
                    .withUsername(s)
                    .roles(roles.stream().toArray(
                        (n) -> {
                            return new String[n];
                        }
                    ))
                    .password(researcher.getHashedPassword())
                    .build();
        }
        else
        {
            throw new UsernameNotFoundException("The researcher with email " + s + " is not registered in the database");
        }**/
        return null;
    }



}


