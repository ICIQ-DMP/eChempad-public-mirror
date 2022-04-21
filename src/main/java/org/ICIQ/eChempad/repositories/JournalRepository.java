/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Journal;
import org.springframework.data.jpa.repository.Query;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public interface JournalRepository extends GenericRepository<Journal, UUID>{

}
