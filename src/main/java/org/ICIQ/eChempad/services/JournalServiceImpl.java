package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.GenericEntity;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.configurations.Security.AclServiceCustomImpl;
import org.ICIQ.eChempad.repositories.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.UUID;

@Service
public class JournalServiceImpl<T extends GenericEntity, S extends Serializable> extends GenericServiceImpl<Journal, UUID> implements JournalService<Journal, UUID> {

    @Autowired
    public JournalServiceImpl(JournalRepository<T, S> journalRepository, AclServiceCustomImpl aclRepository) {
        super(journalRepository, aclRepository);
    }

}
