/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.zkoss.spring.config.ZkScopesConfigurer;


import java.io.Serializable;
import java.util.UUID;

@Scope("desktop")
public interface JournalJPAController<T extends JPAEntityImpl, S extends Serializable> extends GenericJPAController<Journal, UUID> {
}
