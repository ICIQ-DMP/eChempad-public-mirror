package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.entities.Document;
import org.ICIQ.eChempad.entities.ElementPermission;
import org.ICIQ.eChempad.repositories.DocumentRepository;
import org.ICIQ.eChempad.repositories.ElementPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ElementPermissionServiceClass extends GenericServiceClass<ElementPermission, UUID> implements ElementPermissionService{
    @Autowired
    public ElementPermissionServiceClass(ElementPermissionRepository elementPermissionRepository) {
        super(elementPermissionRepository);
    }


}
