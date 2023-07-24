package org.ICIQ.eChempad.controllers.genericJPAControllers;

import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Container;
import org.springframework.context.annotation.Scope;


import java.io.Serializable;
import java.util.UUID;

@Scope("desktop")
public interface ContainerJPAController<T extends EntityImpl, S extends Serializable> extends GenericJPAController<Container, UUID> {
}
