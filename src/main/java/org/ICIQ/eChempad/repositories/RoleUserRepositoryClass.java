package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Researcher;
import org.ICIQ.eChempad.entities.RoleUser;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.UUID;

@Repository
@Transactional
public class RoleUserRepositoryClass extends GenericRepositoryClass<RoleUser, UUID> implements RoleUserRepository{
}
