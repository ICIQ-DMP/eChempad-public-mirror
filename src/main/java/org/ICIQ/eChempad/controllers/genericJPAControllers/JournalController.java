package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.GenericJPAEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;

import java.io.Serializable;
import java.util.UUID;

public interface JournalController<T extends GenericJPAEntity, S extends Serializable> extends GenericController<Journal, UUID> {
}
