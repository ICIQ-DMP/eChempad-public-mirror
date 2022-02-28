package org.ICIQ.eChempad.repositories;

import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.entities.ElementPermission;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.UUID;

@Repository
@Transactional
public class ElementPermissionRepositoryClass extends GenericRepositoryClass<ElementPermission, UUID> implements ElementPermissionRepository{
}
