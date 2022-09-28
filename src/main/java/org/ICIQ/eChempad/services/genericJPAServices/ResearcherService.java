/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.GenericJPAEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;

import java.io.Serializable;
import java.util.UUID;

/**
 * Non-generic functions used to manipulate the in-memory data structures of the researchers. The generic calls are
 * provided in GenericServiceClass
 */
public interface ResearcherService<T extends GenericJPAEntity, S extends Serializable> extends GenericService<Researcher, UUID> {

    /**
     * Same as in the repository, but in this case we delegate to the repository layer
     * @param email email of the selected user
     * @return Returns the userdetails of a user
     */
    Researcher loadUserByUsername(String email);

}
