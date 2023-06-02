/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2023 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Entity;

/**
 * This interface exists to parse Journal, Experiment and Document into one another. This is clearly a design flaw.
 * This is a change that was proposed after I have already written the code for only having 3 nesting levels. Nesting
 * functionality should have been written using a composer pattern. I actually did not do it at first because my initial
 * specification was clearly intended for 3 nesting levels and no more, but after a year the specification changed, and
 * now I need to deal with a very suboptimal implementation.
 */
public interface EntityConversionService {
    /**
     * Transforms the received JPA entity into a Journal.
     *
     * @param entity A JPA entity to be parsed.
     * @return Journal with the metadata of the JPA entity in the parameter.
     */
    Container parseJournal(Entity entity);

    /**
     * Transforms the received JPA entity into a Document.
     *
     * @param entity A JPA entity to be parsed.
     * @return Document with the metadata of the JPA entity in the parameter.
     */
    Document parseDocument(Entity entity);
}
