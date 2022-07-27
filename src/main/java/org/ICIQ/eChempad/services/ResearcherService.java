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
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * Non-generic functions used to manipulate the in-memory data structures of the researchers. The generic calls are
 * provided in GenericServiceClass
 */
public interface ResearcherService<T extends IEntity, S extends Serializable> extends GenericService<Researcher, UUID> {

    /**
     * Same as in the repository, but in this case we delegate to the repository layer
     * @param email email of the selected user
     * @return Returns the userdetails of a user
     */
    UserDetails loadUserByUsername(String email);

    /**
     * Same as in the repository, but in this case we delegate to the repository layer
     * @return Map of all the UserDetails of the application
     */
    Map<UUID, UserDetails> loadAllUserDetails();

}
