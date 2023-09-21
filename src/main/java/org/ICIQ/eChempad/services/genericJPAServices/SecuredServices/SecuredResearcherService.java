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

    @Override
    public <S1 extends Researcher> S1 save(S1 entity) {
        S1 s1 = super.save(entity);

        // Create ACL SID for the passed researcher
        this.aclService.createAcl(new ObjectIdentityImpl(entity.getUsername(), entity.getId()));

        // Save all possible permission against the saved entity with the current logged user
        this.aclService.addPermissionToEntity(s1, true, PermissionBuilder.getFullPermission(), null);

        return s1;
    }
}
