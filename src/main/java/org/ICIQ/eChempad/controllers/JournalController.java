package org.ICIQ.eChempad.controllers;

import org.ICIQ.eChempad.entities.GenericEntity;
import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.entities.Researcher;

import java.io.Serializable;
import java.util.UUID;

public interface JournalController<T extends GenericEntity, S extends Serializable> extends GenericController<Journal, UUID> {
}
