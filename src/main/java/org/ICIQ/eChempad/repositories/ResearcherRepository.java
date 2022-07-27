/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Researcher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.UUID;

/**
 * When extending a JPARepository interface (by extending GenericRepository) DB access methods are automatically
 * IMPLEMENTED regarding the bounded class, the name of the class and / or the name of the method.
 * JPA tries to deduce the suitable implementation by processing the natural language of the class or methods to
 * implement.
 */
@Repository
@Transactional
public interface ResearcherRepository extends GenericRepository<Researcher, UUID>, UserDetailsService{

    /**
     * Internal method that will be used to authenticate users. Basically it transforms a Researcher entity to a
     * UserDetails entity identified by its email. To construct a user details we need to load its username, roles and
     * password.
     * Gets the UserDetails instance of a user if required by using the user repository underneath by using its email.
     * Used to obtain credentials and permissions and perform authentication and authorization.
     *
     * @param email email of the researcher that we are retrieving data from. Unique in the database.
     * @return Returns all the information of a user using and UserDetails class, which is used by spring to manage the
     * user authentication internally.
     */
    @Query(
            value = "SELECT * FROM researcher r WHERE r.username = '?1'",
            nativeQuery = true
    )
    UserDetails loadUserByUsername(String email);

    /**
     * Loads all user details in a single list, which will be used by the authentication manager to know the
     * authentication data of each user, to know how to authenticate to perform the authentication itself by password
     * matching.
     * @return List of all the user details.
     */
    @Query(
            value = "SELECT * FROM researcher ",
            nativeQuery = true
    )
    Map<UUID, UserDetails> loadAllUserDetails();
}
