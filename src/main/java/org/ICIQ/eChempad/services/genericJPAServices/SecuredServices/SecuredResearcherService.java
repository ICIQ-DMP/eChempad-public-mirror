package org.ICIQ.eChempad.services.genericJPAServices.SecuredServices;

import org.ICIQ.eChempad.configurations.security.ACL.PermissionBuilder;
import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;
import org.ICIQ.eChempad.services.genericJPAServices.ResearcherService;
import org.springframework.security.acls.domain.ObjectIdentityImpl;

import java.io.Serializable;
import java.util.UUID;

public class SecuredResearcherService<T extends Researcher, S extends Serializable> extends SecuredGenericServiceImpl<Researcher, UUID> implements SecuredGenericService<Researcher, UUID>, ResearcherService<Researcher, UUID> {
    @Override
    public Researcher loadUserByUsername(String email) {
        return null;
    }

}
