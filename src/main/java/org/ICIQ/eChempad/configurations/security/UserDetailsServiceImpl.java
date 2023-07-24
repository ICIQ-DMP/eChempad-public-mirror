/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
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
