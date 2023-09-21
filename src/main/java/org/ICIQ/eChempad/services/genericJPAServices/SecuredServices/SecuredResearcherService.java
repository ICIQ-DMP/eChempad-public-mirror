package org.ICIQ.eChempad.services.genericJPAServices.SecuredServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.Document;
import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;
import org.ICIQ.eChempad.services.genericJPAServices.DocumentService;
import org.ICIQ.eChempad.services.genericJPAServices.ResearcherService;

import java.io.Serializable;

public class SecuredResearcherService<T extends Researcher, S extends Serializable> extends SecuredGenericServiceImpl<T, S> implements SecuredGenericService<T, S>, ResearcherService<Researcher, S> {
    @Override
    public Researcher loadUserByUsername(String email) {
        return null;
    }
}
