package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.configurations.Security.AclServiceCustomImpl;
import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.GenericEntity;
import org.ICIQ.eChempad.repositories.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.UUID;

@Service
public class AuthorityServiceImpl<T extends GenericEntity, S extends Serializable> extends GenericServiceImpl<Authority, UUID> implements AuthorityService<Authority, UUID> {

    @Autowired
    public AuthorityServiceImpl(AuthorityRepository<T, S> authorityRepository, AclServiceCustomImpl aclRepository) {
        super(authorityRepository, aclRepository);
    }
}

