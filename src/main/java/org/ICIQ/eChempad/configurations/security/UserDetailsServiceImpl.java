/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.configurations.security;

import org.ICIQ.eChempad.configurations.wrappers.UserDetailsImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;
import org.ICIQ.eChempad.repositories.genericJPARepositories.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

/**
 * This class provides a valid implementation of the {@code UserDetailsService} interface, which defines a contract that
 * forces implementor classes to provide a method to load instances of {@code UserDetails}. {@code UserDetails} is a
 * class that wraps all data that a User needs to have in order to perform authentication (credentials such as username
 * and password) and authorization ({@code GrantedAuthorities} that a user has associated with it.
 *
 * @author Institut Català d'Investigació Química (iciq.cat)
 * @author Aleix Mariné-Tena (amarine@iciq.es, github.com/AleixMT)
 * @author Carles Bo Jané (cbo@iciq.es)
 * @author Moisés Álvarez (malvarez@iciq.es)
 * @version 1.0
 * @since 1/3/2023
 */
@Service
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private ResearcherRepository<Researcher, UUID> researcherRepository;

    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Researcher researcher = this.researcherRepository.findByUsername(username);
        if (researcher == null) {
            throw new UsernameNotFoundException(username);
        }

        return new UserDetailsImpl(researcher);
    }

}
