package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.configurations.security.ACL.AclServiceCustomImpl;
import org.ICIQ.eChempad.configurations.security.ACL.PermissionBuilder;
import org.ICIQ.eChempad.entities.genericJPAEntities.EntityImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Researcher;
import org.ICIQ.eChempad.repositories.genericJPARepositories.ResearcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.UUID;

@Service("researcherService")
public class ResearcherServiceImpl<T extends EntityImpl, S extends Serializable>  extends GenericServiceImpl<Researcher, UUID> implements ResearcherService<Researcher, UUID> {

    @Autowired
    public ResearcherServiceImpl(ResearcherRepository<T, S> researcherRepository, AclServiceCustomImpl aclRepository) {
        super(researcherRepository, aclRepository);
    }

    @Override
    public Researcher loadUserByUsername(String email) {
        return ((ResearcherRepository<Researcher, UUID>) (super.genericRepository)).findByUsername(email);
    }

    @Override
    public <S1 extends Researcher> S1 save(S1 entity) {
        // Save the creation date of this entity.
        entity.initCreationDate();

        // Save it in the database so the id field is set
        S1 t = genericRepository.save(entity);

        // Create ACL SID for the passed researcher
        this.aclRepository.createAcl(
                new ObjectIdentityImpl(entity.getUsername(), entity.getId())
        );

        // Save all possible permission against the saved entity with the current logged user
        this.aclRepository.addPermissionToEntity(t, true, PermissionBuilder.getFullPermissions(), null);

        return t;
    }
}


