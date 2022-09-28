package org.ICIQ.eChempad.services.genericJPAServices;

import org.ICIQ.eChempad.entities.genericJPAEntities.GenericJPAEntity;
import org.ICIQ.eChempad.entities.genericJPAEntities.Journal;
import org.ICIQ.eChempad.configurations.security.ACL.AclServiceCustomImpl;
import org.ICIQ.eChempad.repositories.genericJPARepositories.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.UUID;

@Service
public class JournalServiceImpl<T extends GenericJPAEntity, S extends Serializable> extends GenericServiceImpl<Journal, UUID> implements JournalService<Journal, UUID> {

    @Autowired
    public JournalServiceImpl(JournalRepository<T, S> journalRepository, AclServiceCustomImpl aclRepository) {
        super(journalRepository, aclRepository);
    }

}
