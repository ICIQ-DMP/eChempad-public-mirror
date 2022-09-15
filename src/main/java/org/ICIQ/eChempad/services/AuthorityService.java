package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.configurations.Helpers.AclRepositoryImpl;
import org.ICIQ.eChempad.entities.Authority;
import org.ICIQ.eChempad.entities.IEntity;
import org.ICIQ.eChempad.entities.Journal;
import org.ICIQ.eChempad.repositories.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.UUID;

public interface AuthorityService<T extends IEntity, S extends Serializable> extends GenericService<Authority, UUID> {

}
