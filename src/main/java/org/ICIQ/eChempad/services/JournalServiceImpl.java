package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.repositories.AclRepositoryImpl;
import org.ICIQ.eChempad.repositories.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.UUID;

@Service
public class JournalServiceImpl<T extends IEntity, S extends Serializable> extends GenericServiceImpl<Journal, UUID> implements JournalService<Journal, UUID> {

    @Autowired
    public JournalServiceImpl(JournalRepository<T, S> journalRepository, AclRepositoryImpl aclRepository) {
        super(journalRepository, aclRepository);
    }

}
