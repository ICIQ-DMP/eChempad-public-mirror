package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.configurations.Helpers.AclRepositoryImpl;
import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.repositories.AuthorityRepository;
import org.ICIQ.eChempad.repositories.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.UUID;

@Service
public class AuthorityServiceImpl<T extends IEntity, S extends Serializable> extends GenericServiceImpl<Authority, UUID> implements AuthorityService<Authority, UUID> {

    @Autowired
    public AuthorityServiceImpl(AuthorityRepository<T, S> authorityRepository, AclRepositoryImpl aclRepository) {
        super(authorityRepository, aclRepository);
    }
}

