package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Experiment;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;

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
     * @param jpaEntity A JPA entity to be parsed.
     * @return Journal with the metadata of the JPA entity in the parameter.
     */
    Journal parseJournal(JPAEntity jpaEntity);

    /**
     * Transforms the received JPA entity into an Experiment.
     *
     * @param jpaEntity A JPA entity to be parsed.
     * @return Experiment with the metadata of the JPA entity in the parameter.
     */
    Experiment parseExperiment(JPAEntity jpaEntity);

    /**
     * Transforms the received JPA entity into a Document.
     *
     * @param jpaEntity A JPA entity to be parsed.
     * @return Document with the metadata of the JPA entity in the parameter.
     */
    Document parseDocument(JPAEntity jpaEntity);
}
