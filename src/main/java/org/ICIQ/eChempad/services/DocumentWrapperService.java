package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.genericJPAEntities.DocumentWrapper;
import org.ICIQ.eChempad.entities.genericJPAEntities.Experiment;
import org.ICIQ.eChempad.entities.genericJPAEntities.JPAEntityImpl;
import org.ICIQ.eChempad.services.genericJPAServices.GenericService;

import java.io.Serializable;
import java.util.UUID;

public interface DocumentWrapperService<T extends JPAEntityImpl, S extends Serializable> extends GenericService<DocumentWrapper, UUID> {
}
