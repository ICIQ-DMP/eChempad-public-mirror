package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.configurations.security.ACL.AclServiceCustomImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.Authority;
import org.ICIQ.eChempad.entities.genericJPAEntities.GenericJPAEntity;
import org.ICIQ.eChempad.repositories.genericJPARepositories.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.UUID;

@Service
public class AuthorityServiceImpl<T extends GenericJPAEntity, S extends Serializable> extends GenericServiceImpl<Authority, UUID> implements AuthorityService<Authority, UUID> {

    @Autowired
    public AuthorityServiceImpl(AuthorityRepository<T, S> authorityRepository, AclServiceCustomImpl aclRepository) {
        super(authorityRepository, aclRepository);
    }
}

